package com.maherlabbad.EVChargingSystem.Viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.ChargingSession
import com.maherlabbad.EVChargingSystem.Models.Reservation
import com.maherlabbad.EVChargingSystem.Models.Wallet
import com.maherlabbad.EVChargingSystem.Models.WalletTransaction
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID.randomUUID

class SessionDetailsViewModel : ViewModel() {

    private val _session = MutableStateFlow<ChargingSession?>(null)
    val session: StateFlow<ChargingSession?> = _session.asStateFlow()

    // YENİ: Rezervasyonun tarih ve saatini tutmak için eklendi
    private val _reservation = MutableStateFlow<Reservation?>(null)
    val reservation: StateFlow<Reservation?> = _reservation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchSessionDetails(reservationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // 1. Şarj oturumunu (ChargingSession) çek
                val sessionData = SupabaseNetwork.client
                    .postgrest["ChargingSession"]
                    .select {
                        filter {
                            eq("ReservationID", reservationId)
                        }
                    }
                    .decodeSingleOrNull<ChargingSession>()

                _session.value = sessionData

                // 2. Rezervasyonun kendisini çek (Tarih ve Saat bilgisi için)
                val reservationData = SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .select {
                        filter {
                            eq("ReservationID", reservationId)
                        }
                    }
                    .decodeSingleOrNull<Reservation>()

                _reservation.value = reservationData

                // Eğer şarj datası boş dönerse henüz şarj edilmemiştir
                if (sessionData == null) {
                    _errorMessage.value = "Bu rezervasyon için henüz bir şarj oturumu başlatılmamış."
                }

            } catch (e: Exception) {
                _errorMessage.value = "Detaylar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cancelReservation() {
        val currentReservation = _reservation.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                // --- FR13: 12 SAAT KURALI HESAPLAMASI ---
                // Rezervasyon tarih ve saatini birleştirip parse et
                val resDateTimeStr = "${currentReservation.date} ${currentReservation.startTime}"
                val resDateTime = LocalDateTime.parse(resDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"))
                val now = LocalDateTime.now()

                // İki zaman arasındaki saat farkını bul
                val hoursUntilReservation = ChronoUnit.HOURS.between(now, resDateTime)

                var penaltyApplied = false
                val penaltyAmount = 50.0 // DR05 Kuralı (Min bakiye cezası)

                // Eğer 12 saatten az kaldıysa (ve geçmiş bir zaman değilse) CEZA KES!
                if (hoursUntilReservation in 0..11) {
                    penaltyApplied = true

                    // 1. Cüzdan bakiyesini düşür
                    val currentWallet = SupabaseNetwork.client
                        .postgrest["Wallet"]
                        .select { filter { eq("UserID", userId) } }
                        .decodeSingleOrNull<Wallet>()

                    if (currentWallet != null) {
                        val newBalance = currentWallet.balance - penaltyAmount
                        SupabaseNetwork.client.postgrest["Wallet"].update({ set("Balance", newBalance) }) {
                            filter { eq("UserID", userId) }
                        }
                    }

                    // 2. Ceza işlemini Transaction (Fatura) geçmişine yaz
                    val tx = WalletTransaction(
                        transactionID = randomUUID().toString(),
                        userID = userId,
                        amount = -penaltyAmount,
                        type = "Penalty",
                        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        description = "Late Cancellation Penalty (< 12 hours)",
                        reservationId = currentReservation.reservationID
                    )
                    SupabaseNetwork.client.postgrest["WalletTransactions"].insert(tx)
                }

                // --- REZERVASYONU İPTAL ET ---
                SupabaseNetwork.client.postgrest["Reservation"]
                    .update({ set("Status", "Cancelled") }) {
                        filter { eq("ReservationID", currentReservation.reservationID) }
                    }

                // Ekrana bilgi mesajı bas
                if (penaltyApplied) {
                    _infoMessage.value = "Rezervasyon iptal edildi. 12 saat kuralı ihlal edildiği için 50 TL ceza kesildi."
                } else {
                    _infoMessage.value = "Rezervasyon ücretsiz olarak iptal edildi."
                }

                // Sayfayı yenile
                fetchSessionDetails(currentReservation.reservationID)

            } catch (e: Exception) {
                _errorMessage.value = "İptal işlemi başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}
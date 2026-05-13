package com.maherlabbad.EVChargingSystem.Viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Models.ChargingSession
import com.maherlabbad.EVChargingSystem.Models.Reservation
import com.maherlabbad.EVChargingSystem.Models.Wallet
import com.maherlabbad.EVChargingSystem.Models.WalletTransaction
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class ChargingState {
    IDLE, STARTING, CHARGING, COMPLETED
}

@RequiresApi(Build.VERSION_CODES.O)
class ActiveChargingViewModel : ViewModel() {

    // --- State'ler ---

    private val _chargingState = MutableStateFlow(ChargingState.IDLE)
    val chargingState: StateFlow<ChargingState> = _chargingState.asStateFlow()

    private val _currentSession = MutableStateFlow<ChargingSession?>(null)
    val currentSession: StateFlow<ChargingSession?> = _currentSession.asStateFlow()

    private val _batteryPercentage = MutableStateFlow(42) // Simüle edilmiş başlangıç şarjı
    val batteryPercentage: StateFlow<Int> = _batteryPercentage.asStateFlow()

    private val _addedKwh = MutableStateFlow(0.0)
    val addedKwh: StateFlow<Double> = _addedKwh.asStateFlow()

    private val _currentCost = MutableStateFlow(0.0)
    val currentCost: StateFlow<Double> = _currentCost.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Faz 1: Dinamik Birim Fiyat State
    private val _activeChargerUnitPrice = MutableStateFlow(0.0)
    val activeChargerUnitPrice: StateFlow<Double> = _activeChargerUnitPrice.asStateFlow()

    // Faz 2: Bakiye Takibi State
    private val _userBalance = MutableStateFlow(0.0)
    val userBalance: StateFlow<Double> = _userBalance.asStateFlow()

    private var chargingJob: Job? = null
    private var activeChargerId = ""

    private val _hasActiveReservation = MutableStateFlow(false)
    val hasActiveReservation: StateFlow<Boolean> = _hasActiveReservation.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0)
    val elapsedTimeSeconds: StateFlow<Int> = _elapsedTimeSeconds.asStateFlow()

    // O anki aktif Rezervasyon ID'sini hafızada tutmak için
    private var _activeReservation = MutableStateFlow<Reservation?>(null)
    val activeReservation: StateFlow<Reservation?> = _activeReservation.asStateFlow()

    // --- GÜVENLİK VE HANDSHAKE STATE'LERİ ---
    private val _isQrVerified = MutableStateFlow(false)
    val isQrVerified: StateFlow<Boolean> = _isQrVerified.asStateFlow()

    private val _isCablePlugged = MutableStateFlow(false)
    val isCablePlugged: StateFlow<Boolean> = _isCablePlugged.asStateFlow()

    fun verifyQrCode() {
        _isQrVerified.value = true
    }

    fun plugCable() {
        _isCablePlugged.value = true
    }

    // --- 1. EKRAN AÇILDIĞINDA REZERVASYONU VE OTURUMU BUL ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadActiveReservationAndSession() {

        if (_chargingState.value == ChargingState.CHARGING) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    _errorMessage.value = "Kullanıcı oturumu bulunamadı."
                    return@launch
                }

                // Faz 2: Bakiye Takibi - Cüzdan verisini çek
                val wallet = SupabaseNetwork.client
                    .postgrest["Wallet"]
                    .select { filter { eq("UserID", currentUserId) } }
                    .decodeSingleOrNull<Wallet>()
                _userBalance.value = wallet?.balance ?: 0.0

                // 1. Kullanıcının rezervasyonlarını çek
                val userReservations = SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .select {
                        filter { eq("UserID", currentUserId) }
                    }
                    .decodeList<Reservation>()

                // 2. Bekleyen (Pending) veya Aktif (Active) olan tek rezervasyonu bul
                val activeOrPendingReservation = userReservations.firstOrNull {
                    it.status.equals("Pending", ignoreCase = true) ||
                            it.status.equals("Active", ignoreCase = true)
                }

                if (activeOrPendingReservation == null) {
                    _errorMessage.value = "Aktif bir rezervasyonunuz bulunmuyor."
                    return@launch
                }
                _hasActiveReservation.value = true
                activeChargerId = activeOrPendingReservation.chargerID
                _activeReservation.value = activeOrPendingReservation

                // Faz 1: Dinamik Birim Fiyat - Charger bilgilerinden birim fiyatı çek
                val charger = SupabaseNetwork.client
                    .postgrest["Charger"]
                    .select { filter { eq("ChargerID", activeChargerId) } }
                    .decodeSingleOrNull<Charger>()
                _activeChargerUnitPrice.value = charger?.unitPrice ?: 8.50

                // 3. Bu rezervasyona ait oluşturulmuş bir Session var mı?
                val session = SupabaseNetwork.client
                    .postgrest["ChargingSession"]
                    .select {
                        filter { eq("ReservationID", activeOrPendingReservation.reservationID) }
                    }
                    .decodeSingleOrNull<ChargingSession>()

                _currentSession.value = session

                if (session?.connectionStatus == "CHARGING") {
                    _chargingState.value = ChargingState.CHARGING
                    _addedKwh.value = session.energyConsumed
                    _currentCost.value = session.finalCost
                    startSimulationLoop()
                } else {
                    _chargingState.value = ChargingState.IDLE
                }

            } catch (e: Exception) {
                _errorMessage.value = "Veriler yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 2. ŞARJI BAŞLAT ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun startCharging() {
        val resId = activeReservation.value?.reservationID ?: ""
        if (resId.isEmpty() || _chargingState.value == ChargingState.CHARGING || activeChargerId.isEmpty()) return

        // Faz 2: Başlatma Engeli - Minimum bakiye kontrolü
        if (_userBalance.value < 50.0) {
            _errorMessage.value = "Şarjı başlatmak için en az 50 TL bakiyeniz olmalıdır."
            return
        }

        isReservationTimeValid(
            startTimeStr = activeReservation.value?.startTime ?: "00:00:00",
            endTimeStr = activeReservation.value?.endTime ?: "00:00:00",
            dateStr = activeReservation.value?.date ?: "1970-01-01"
        ).let { (isValid, message) ->
            if (!isValid) {
                _errorMessage.value = "Şarjı başlatmak için zaman kısıtlamalarına uymanız gerekiyor: $message"
                return
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _chargingState.value = ChargingState.STARTING
            delay(1500)

            try {
                val newSession = ChargingSession(
                    reservationID = resId,
                    energyConsumed = 0.0,
                    finalCost = 0.0,
                    connectionStatus = "CHARGING",
                    sessionID = UUID.randomUUID().toString()
                )

                val createdSession = SupabaseNetwork.client
                    .postgrest["ChargingSession"]
                    .insert(newSession) { select() }
                    .decodeSingle<ChargingSession>()

                _currentSession.value = createdSession
                
                SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .update({ set("Status", "Active") }) {
                        filter { eq("ReservationID", resId) }
                    }

                SupabaseNetwork.client.postgrest["Charger"]
                    .update({ set("Status", "In Use") }) {
                        filter { eq("ChargerID", activeChargerId) }
                    }

                _chargingState.value = ChargingState.CHARGING
                startSimulationLoop()

            } catch (e: Exception) {
                _chargingState.value = ChargingState.IDLE
                _errorMessage.value = "Şarj başlatılamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- SAYAÇ SİMÜLASYONU ---
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startSimulationLoop() {
        chargingJob?.cancel()
        chargingJob = viewModelScope.launch {
            while (_chargingState.value == ChargingState.CHARGING && _batteryPercentage.value < 100) {
                delay(1000)
                _elapsedTimeSeconds.value += 1
                val newKwh = _addedKwh.value + 0.05
                _addedKwh.value = Math.round(newKwh * 100) / 100.0
                
                // Faz 1: Simülasyon Hesaplaması - Dinamik birim fiyat
                _currentCost.value = Math.round((_addedKwh.value * _activeChargerUnitPrice.value) * 100) / 100.0

                // Faz 2: Canlı Kontrol Döngüsü - Circuit Breaker
                if (_currentCost.value >= _userBalance.value) {
                    stopChargingAndPay()
                    break
                }

                if (_elapsedTimeSeconds.value % 10 == 0) {
                    _batteryPercentage.value += 1
                }
            }
            if (_batteryPercentage.value >= 100) {
                stopChargingAndPay()
            }
        }
    }

    // --- 3. ŞARJI DURDUR VE ÖDE ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun stopChargingAndPay() {
        val currentSessionId = _currentSession.value?.sessionID ?: return
        val activeResId = _activeReservation.value?.reservationID ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            chargingJob?.cancel()
            _chargingState.value = ChargingState.COMPLETED

            try {
                // Faz 3: Session Güncellemesi
                SupabaseNetwork.client
                    .postgrest["ChargingSession"]
                    .update(
                        {
                            set("ConnectionStatus", "COMPLETED")
                            set("EnergyConsumed", _addedKwh.value)
                            set("FinalCost", _currentCost.value)
                        }
                    ) {
                        filter { eq("SessionID", currentSessionId) }
                    }

                // Faz 3: Rezervasyon Durumu "Completed"
                SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .update({ set("Status", "Completed") }) {
                        filter { eq("ReservationID", activeResId) }
                    }

                // Faz 3: Charger durumunu "Available" yap
                SupabaseNetwork.client.postgrest["Charger"]
                    .update({ set("Status", "Available") }) {
                        filter { eq("ChargerID", activeChargerId) }
                    }

                // Faz 3: İşlem Kaydı (WalletTransactions)
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                val tx = WalletTransaction(
                    transactionID = UUID.randomUUID().toString(),
                    userID = userId,
                    amount = -_currentCost.value,
                    type = "Charging",
                    timestamp = timestamp,
                    description = "VoltCharge Charging Session (${_addedKwh.value} kWh)"
                )
                SupabaseNetwork.client.postgrest["WalletTransactions"].insert(tx)

                // Faz 3: Cüzdan Güncellemesi (Harcanan tutarı düş)
                val currentWallet = SupabaseNetwork.client
                    .postgrest["Wallet"]
                    .select { filter { eq("UserID", userId) } }
                    .decodeSingleOrNull<Wallet>()

                if (currentWallet != null) {
                    val newBalance = currentWallet.balance - _currentCost.value
                    SupabaseNetwork.client.postgrest["Wallet"].update({ set("Balance", newBalance) }) {
                        filter { eq("UserID", userId) }
                    }
                    _userBalance.value = newBalance
                }


            } catch (e: Exception) {
                _errorMessage.value = "İşlem sonlandırılamadı: ${e.localizedMessage}"
            } finally {
                _addedKwh.value = 0.0
                _currentCost.value = 0.0
                _chargingState.value = ChargingState.IDLE
                _isQrVerified.value = false
                _isCablePlugged.value = false
                _isLoading.value = false
            }
        }
    }

    fun isReservationTimeValid(startTimeStr: String, endTimeStr: String, dateStr: String): Pair<Boolean, String> {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            val reservationDate = LocalDate.parse(dateStr, dateFormatter)
            val startTime = LocalTime.parse(startTimeStr, timeFormatter)
            val endTime = LocalTime.parse(endTimeStr, timeFormatter)

            val today = LocalDate.now()
            val currentTime = LocalTime.now()

            when {
                today.isAfter(reservationDate) -> Pair(false, "Randevu tarihiniz geçti.")
                currentTime.isBefore(startTime) -> Pair(false, "Henüz başlama saati gelmedi ($startTimeStr).")
                currentTime.isAfter(endTime) -> Pair(false, "Randevu süreniz doldu.")
                else -> Pair(true, "Başarılı")
            }
        } catch (e: Exception) {
            Pair(false, "Zaman formatı geçersiz.")
        }
    }
}

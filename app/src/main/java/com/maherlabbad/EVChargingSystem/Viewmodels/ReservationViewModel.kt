package com.maherlabbad.EVChargingSystem.Viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.Reservation
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ReservationViewModel : ViewModel() {

    // Kullanıcının rezervasyon listesi
    private val _myReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val myReservations: StateFlow<List<Reservation>> = _myReservations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Rezervasyon başarılı olduğunda sayfayı değiştirmek (veya pop-up göstermek) için
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    init {
        fetchMyReservations()
    }

    // --- REZERVASYON OLUŞTURMA (INSERT) ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun createReservation(chargerId: String, date: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isSuccess.value = false

            try {

                val hasActiveOrPending = _myReservations.value.any {
                    it.status.equals("Pending", ignoreCase = true) || it.status.equals("Active", ignoreCase = true)
                }

                if (hasActiveOrPending) {
                    _errorMessage.value = "Zaten bekleyen veya aktif bir rezervasyonunuz bulunuyor. Yeni bir işlem yapmadan önce mevcut rezervasyonunuzu tamamlamalı veya iptal etmelisiniz."
                    return@launch
                }


                // --- YENİ: SAAT KONTROLÜ (MAX 2 SAAT KURALI) ---
                val parsedStartTime = LocalTime.parse(startTime)
                val parsedEndTime = LocalTime.parse(endTime)

                val duration = Duration.between(parsedStartTime, parsedEndTime)
                val durationInMinutes = duration.toMinutes()

                // Bitiş saati, başlangıç saatinden önce veya aynı olamaz
                if (durationInMinutes <= 0) {
                    _errorMessage.value = "Bitiş saati, başlangıç saatinden ileri bir zaman olmalıdır."
                    return@launch
                }

                // Maksimum 2 saat (120 dakika) kuralı
                if (durationInMinutes > 120) {
                    _errorMessage.value = "Maksimum 2 saatlik rezervasyon yapabilirsiniz."
                    return@launch
                }

                // Supabase 'time' formatı için saniyeleri de ekleyerek formatlıyoruz ("14:30:00")
                val formattedStartTime = parsedStartTime.format(DateTimeFormatter.ofPattern("HH:mm:00"))
                val formattedEndTime = parsedEndTime.format(DateTimeFormatter.ofPattern("HH:mm:00"))

                // 1. Giriş yapmış kullanıcının gerçek ID'sini al
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id

                if (currentUserId == null) {
                    _errorMessage.value = "Lütfen önce giriş yapın."
                    return@launch
                }

                val currentWallet = SupabaseNetwork.client
                    .postgrest["Wallet"]
                    .select { filter { eq("UserID", currentUserId) } }
                    .decodeSingleOrNull<com.maherlabbad.EVChargingSystem.Models.Wallet>() // Kendi Wallet modelinin yolunu kontrol et

                // Eğer cüzdan yoksa veya bakiye 50 TL'den azsa işlemi durdur
                if (currentWallet == null || currentWallet.balance < 50.0) {
                    _errorMessage.value = "Rezervasyon yapabilmek için cüzdanınızda en az 50 TL bulunmalıdır. Lütfen bakiye yükleyin."
                    return@launch
                }

                val existingReservations = SupabaseNetwork.client.postgrest["Reservation"]
                    .select {
                        filter {
                            eq("ChargerID", chargerId)
                            eq("Date", date)
                        }
                    }.decodeList<Reservation>()

                // Çakışma var mı diye kontrol et
                val isOverlapping = existingReservations.any { existingRes ->
                    val status = existingRes.status

                    // İptal edilen (Cancelled) veya Biten (Completed) rezervasyonlar saati meşgul etmez
                    if (status.equals("Cancelled", ignoreCase = true) || status.equals("Completed", ignoreCase = true)) {
                        false
                    } else {
                        // Veritabanından gelen "14:30:00" formatını "14:30" olarak alıyoruz
                        val existStart = LocalTime.parse(existingRes.startTime.take(5))
                        val existEnd = LocalTime.parse(existingRes.endTime.take(5))

                        // Çakışma Formülü: Yeni Başlangıç < Eski Bitiş VE Yeni Bitiş > Eski Başlangıç
                        parsedStartTime.isBefore(existEnd) && parsedEndTime.isAfter(existStart)
                    }
                }

                // Eğer çakışma varsa işlemi durdur ve hata ver!
                if (isOverlapping) {
                    _errorMessage.value = "Bu cihaz seçtiğiniz saatlerde başka bir kullanıcı tarafından rezerve edilmiştir. Lütfen farklı bir saat seçin."
                    return@launch
                }

                // 3. Veritabanına yazılacak Reservation objesini hazırla
                val newReservation = Reservation(
                    reservationID = UUID.randomUUID().toString(),
                    date = date,
                    startTime = formattedStartTime, // Formatlanmış halini gönderiyoruz
                    endTime = formattedEndTime,     // Formatlanmış halini gönderiyoruz
                    status = "Pending", // Rezervasyon ilk yapıldığında Pending (Bekliyor) olmalı
                    userID = currentUserId,
                    chargerID = chargerId
                )

                // 4. Supabase'e gönder (INSERT işlemi)
                SupabaseNetwork.client.postgrest["Reservation"].insert(newReservation)

                _isSuccess.value = true
                _myReservations.value += newReservation

            } catch (e: Exception) {
                // Eğer tarih formatı yanlış gelirse buraya düşer
                _errorMessage.value = "Geçersiz saat formatı veya bağlantı hatası: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- REZERVASYONLARIMI GETİR (SELECT) ---
    fun fetchMyReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                // Sadece giriş yapan kullanıcıya ait rezervasyonları çek
                val data = SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeList<Reservation>()

                // Tarihe veya saate göre sıralamak istersen burada .sortedByDescending { it.date } yapabilirsin
                _myReservations.value = data

            } catch (e: Exception) {
                println("Rezervasyon çekme hatası: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccessState() {
        _isSuccess.value = false
    }
}
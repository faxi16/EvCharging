package com.maherlabbad.EVChargingSystem.Viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.User
import com.maherlabbad.EVChargingSystem.Models.UserStatistics
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _statistics = MutableStateFlow<UserStatistics?>(null)
    val statistics: StateFlow<UserStatistics?> = _statistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _issueSubmitStatus = MutableStateFlow<String?>(null)
    val issueSubmitStatus: StateFlow<String?> = _issueSubmitStatus.asStateFlow()

    private val _isSubmittingIssue = MutableStateFlow(false)
    val isSubmittingIssue: StateFlow<Boolean> = _isSubmittingIssue.asStateFlow()

    init {
        fetchProfileData()
    }
    // ARTIK PARAMETRE ALMIYORUZ! () İÇİ BOŞ.
    fun fetchProfileData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // SİHİRLİ SATIR: Telefonun içinde şu an oturumu açık olan kullanıcının ID'sini Supabase'den al!
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id

                // Eğer kullanıcı giriş yapmamışsa (ID null ise) işlemi iptal et
                if (currentUserId == null) {
                    println("Hata: Giriş yapmış bir kullanıcı bulunamadı!")
                    return@launch
                }

                // 1. Kullanıcı bilgilerini çek (Artık currentUserId kullanıyoruz)
                val fetchedUser = SupabaseNetwork.client
                    .postgrest["User"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeSingleOrNull<User>()

                _user.value = fetchedUser

                // 2. İstatistikleri çek (Yine currentUserId ile)
                val fetchedStats = SupabaseNetwork.client
                    .postgrest["user_statistics_view"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeSingleOrNull<UserStatistics>()

                _statistics.value = fetchedStats

            } catch (e: Exception) {
                println("Supabase Hatası (Profil): ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }




    // Sorun Bildirme Fonksiyonu (FR11)
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitIssue(locationorID: String, description: String) {
        if (locationorID.isBlank() || description.isBlank()) {
            _issueSubmitStatus.value = "Lütfen tüm alanları doldurun."
            return
        }

        viewModelScope.launch {
            _isSubmittingIssue.value = true
            _issueSubmitStatus.value = null
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                val isUuid = try {
                    UUID.fromString(locationorID)
                    true
                } catch (e: Exception) {
                    false
                }

                // Eğer UUID değilse veritabanı çökmesin diye Description'a ekliyoruz
                val finalStationId = if (isUuid) locationorID else null
                val finalDescription = if (isUuid) description else "Location: $locationorID | Detail: $description"
                // Veritabanına gönderilecek veriyi haritalandır

                val issueData = mapOf(
                    "IssueID" to UUID.randomUUID().toString(),
                    "UserID" to userId,
                    "StationID" to finalStationId,
                    "Description" to finalDescription,
                    "Status" to "Pending",
                    "Timestamp" to OffsetDateTime.now().toString()
                )

                // Supabase Issue tablosuna kaydet
                SupabaseNetwork.client.postgrest["Issue"].insert(issueData)

                _issueSubmitStatus.value = "Success"

                // 3 saniye sonra başarı mesajını ekrandan temizle
                delay(3000)
                _issueSubmitStatus.value = null

            } catch (e: Exception) {
                _issueSubmitStatus.value = "Hata: ${e.localizedMessage}"
            } finally {
                _isSubmittingIssue.value = false
            }
        }
    }

    fun resetIssueStatus() {
        _issueSubmitStatus.value = null
    }

    // Çıkış Yapma Fonksiyonu
    fun logoutUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseNetwork.client.auth.signOut()
                onSuccess() // Çıkış başarılıysa navigasyon callback'ini tetikle
            } catch (e: Exception) {
                println("Çıkış yapılırken hata oluştu: ${e.localizedMessage}")
            }
        }
    }

}
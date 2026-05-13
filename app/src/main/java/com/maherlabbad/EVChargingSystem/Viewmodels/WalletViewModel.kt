package com.maherlabbad.EVChargingSystem.Viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.Wallet
import com.maherlabbad.EVChargingSystem.Models.WalletTransaction
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID.randomUUID

class WalletViewModel : ViewModel() {

    // Kullanıcının cüzdan bilgisi (Bakiye vs.)
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Para yükleme başarılı olduğunda arayüze haber vermek için
    private val _isTopUpSuccess = MutableStateFlow(false)
    val isTopUpSuccess: StateFlow<Boolean> = _isTopUpSuccess.asStateFlow()
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    init {
        // ViewModel çağrıldığı an kullanıcının bakiyesini çek
        fetchWallet()
        fetchTransactions()
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                val fetchedList = SupabaseNetwork.client
                    .postgrest["WalletTransactions"] // Tablo adı
                    .select {
                        filter { eq("UserID", currentUserId) }
                    }
                    .decodeList<WalletTransaction>()

                // En yeni işlemler en üstte görünsün diye Timestamp'e göre ters sıralıyoruz
                _transactions.value = fetchedList.sortedByDescending { it.timestamp }

            } catch (e: Exception) {
                println("İşlem geçmişi çekilemedi: ${e.localizedMessage}")
            }
        }
    }

    // --- 1. CÜZDANI GETİR (READ) ---
    fun fetchWallet() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // SİHİRLİ SATIR: Yine dışarıdan parametre almıyoruz, kendi kimliğini buluyor!
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id

                if (currentUserId == null) return@launch

                var fetchedWallet = SupabaseNetwork.client
                    .postgrest["Wallet"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeSingleOrNull<Wallet>() // Cüzdan tek bir satırdır, list değildir.

                if (fetchedWallet == null) {
                    val newWallet = Wallet(
                        // ID'yi otomatik artan yapmıyorsan veya UUID veriyorsan modeline göre ayarla
                        userID = currentUserId,
                        balance = 0.0,
                    )
                    // Supabase'e bu yeni cüzdanı kaydet (.select() diyerek kaydettiği veriyi geri döndürmesini istiyoruz)
                    fetchedWallet = SupabaseNetwork.client
                        .postgrest["Wallet"]
                        .insert(newWallet) { select() }
                        // Not: Supabase modelinde upsert() fonksiyonunu da kullanabilirsin.
                        .decodeSingle<Wallet>()
                }

                _wallet.value = fetchedWallet

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Cüzdan bilgileri alınamadı."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 2. BAKİYE YÜKLE (UPDATE) ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun topUpBalance(amountToAdd: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isTopUpSuccess.value = false

            try {
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    _errorMessage.value = "Lütfen giriş yapın."
                    return@launch
                }

                // Güncel cüzdanı kontrol et
                val currentWallet = _wallet.value
                if (currentWallet != null) {
                    // Yeni bakiyeyi hesapla (Mevcut Bakiye + Eklenen Tutar)
                    val newBalance = currentWallet.balance + amountToAdd

                    // Supabase'de UPDATE işlemi yapıyoruz (Insert değil, çünkü satır zaten var)
                    SupabaseNetwork.client
                        .postgrest["Wallet"]
                        .update(
                            {
                                set("Balance", newBalance)
                            }
                        ) {
                            filter {
                                eq("UserID", currentUserId) // Sadece bu kullanıcının cüzdanını güncelle
                            }
                        }
                    val formattedTime = java.time.OffsetDateTime.now().toString()
                    val transaction = WalletTransaction(
                        transactionID = randomUUID().toString(),
                        userID = currentUserId,
                        amount = amountToAdd,
                        type = "TopUp",
                        timestamp = formattedTime,
                        description = "balance topup"
                    )

                    SupabaseNetwork.client
                        .postgrest["WalletTransactions"]
                        .insert(
                            transaction
                        )
                    _isTopUpSuccess.value = true
                    _wallet.value = currentWallet.copy(balance = newBalance)
                    _transactions.value += transaction
                } else {
                    _errorMessage.value = "Cüzdan bulunamadı. Lütfen destekle iletişime geçin."
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Bakiye yüklenirken hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccessState() {
        _isTopUpSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.WalletTransaction
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel : ViewModel() {

    private val _transaction = MutableStateFlow<WalletTransaction?>(null)
    val transaction: StateFlow<WalletTransaction?> = _transaction.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Fişi veritabanından çekme fonksiyonu
    fun loadTransactionDetails(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Supabase'den spesifik bir ID'ye sahip işlemi getir
                val data = SupabaseNetwork.client
                    .postgrest["WalletTransactions"]
                    .select {
                        filter {
                            eq("TransactionID", transactionId)
                        }
                    }
                    .decodeSingleOrNull<WalletTransaction>()

                _transaction.value = data

            } catch (e: Exception) {
                android.util.Log.e("TransactionDetails", "Hata: ${e.message}", e)
                _errorMessage.value = "Fatura yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
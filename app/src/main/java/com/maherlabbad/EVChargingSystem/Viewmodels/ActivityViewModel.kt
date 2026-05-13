package com.maherlabbad.EVChargingSystem.Viewmodels

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

class ActivityViewModel : ViewModel() {

    private val _activities = MutableStateFlow<List<Reservation>>(emptyList())
    val activities: StateFlow<List<Reservation>> = _activities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id
                if (currentUserId == null) {
                    _errorMessage.value = "Lütfen giriş yapın."
                    return@launch
                }

                // Sadece bu kullanıcıya ait işlemleri çek
                val data = SupabaseNetwork.client
                    .postgrest["Reservation"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeList<Reservation>()

                // En yeni tarih en üstte olacak şekilde sırala
                val sortedData = data.sortedWith(compareByDescending<Reservation> { it.date }.thenByDescending { it.startTime })

                _activities.value = sortedData

            } catch (e: Exception) {
                _errorMessage.value = "Aktiviteler yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
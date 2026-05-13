package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.ActiveVehicleSession
import com.maherlabbad.EVChargingSystem.Models.ElectricVehicle
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyVehiclesViewModel : ViewModel() {

    // Kullanıcının araç listesi
    private val _vehicles = MutableStateFlow<List<ElectricVehicle>>(emptyList())
    val vehicles: StateFlow<List<ElectricVehicle>> = _vehicles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activeVehicleId = MutableStateFlow<String?>(null)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Araç ekleme başarılı olduğunda sayfayı kapatmak/yönlendirmek için
    private val _isAddSuccess = MutableStateFlow(false)
    val isAddSuccess: StateFlow<Boolean> = _isAddSuccess.asStateFlow()

    init {
        // ViewModel ilk oluştuğunda kullanıcının araçlarını otomatik çek!
        fetchMyVehicles()
    }

    fun setActiveVehicle(vehicleId: String, connectorType: String) {
        ActiveVehicleSession.setActiveVehicle(vehicleId,connectorType)
    }

    fun deleteVehicle(plateNumber: String) {
        viewModelScope.launch {
            val id = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch
            _isLoading.value = true
            _errorMessage.value = null

            try {
                SupabaseNetwork.client.postgrest["ElectricVehicle"].delete {
                    filter {
                        eq("PlateNumber", plateNumber)
                        eq("UserID",id )
                    }
                }
                fetchMyVehicles()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Araç silinirken bir hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 1. ARAÇLARI GETİR (READ) ---
    fun fetchMyVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Telefonun içindeki oturumdan gerçek kullanıcının ID'sini al
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id

                if (currentUserId == null) return@launch // Giriş yapılmamışsa iptal et

                val fetchedVehicles = SupabaseNetwork.client
                    .postgrest["ElectricVehicle"]
                    .select {
                        filter {
                            eq("UserID", currentUserId)
                        }
                    }
                    .decodeList<ElectricVehicle>()

                if (_activeVehicleId.value == null && fetchedVehicles.isNotEmpty()) _activeVehicleId.value = fetchedVehicles[0].plateNumber

                _vehicles.value = fetchedVehicles

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Araçlar yüklenemedi."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 2. YENİ ARAÇ EKLE (INSERT) ---
    fun addVehicle(plateNumber: String, brand: String, model: String, batteryCapacity: Double, connectorType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isAddSuccess.value = false

            try {
                // Yine gerçek kullanıcının ID'sini al
                val currentUserId = SupabaseNetwork.client.auth.currentUserOrNull()?.id

                if (currentUserId == null) {
                    _errorMessage.value = "Lütfen giriş yapın."
                    return@launch
                }

                // Veritabanına gönderilecek yeni araç objesini oluştur
                val newVehicle = ElectricVehicle(
                    plateNumber = plateNumber,
                    brand = brand,
                    model = model,
                    batteryCapacity = batteryCapacity,
                    connectorType = connectorType,
                    userId = currentUserId
                )

                // Supabase'e yaz (Insert)
                SupabaseNetwork.client.postgrest["ElectricVehicle"].insert(newVehicle)

                // Başarılı olduysa UI'ı uyar ve listeyi arkadan yenile!
                _isAddSuccess.value = true
                fetchMyVehicles()

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Araç eklenirken bir hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Başarı durumunu sıfırlamak için (Sayfa değişimlerinde tetiklenmesin diye)
    fun resetSuccessState() {
        _isAddSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
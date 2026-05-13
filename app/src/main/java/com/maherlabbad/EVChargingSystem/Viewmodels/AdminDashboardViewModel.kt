package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.AdminPeakHour
import com.maherlabbad.EVChargingSystem.Models.AdminStationUtilization
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {

    private val _utilizations = MutableStateFlow<List<AdminStationUtilization>>(emptyList())
    val utilizations: StateFlow<List<AdminStationUtilization>> = _utilizations.asStateFlow()

    private val _peakHours = MutableStateFlow<List<AdminPeakHour>>(emptyList())
    val peakHours: StateFlow<List<AdminPeakHour>> = _peakHours.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- Ağ Genel (Network-Wide) Toplamları ---
    val totalNetworkRevenue = MutableStateFlow(0.0)
    val totalNetworkPenalties = MutableStateFlow(0.0)
    val totalNetworkSessions = MutableStateFlow(0)
    val totalNetworkCancellations = MutableStateFlow(0)

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. İstasyon Kullanım (Utilization) View'unu Çek
                val utilizationData = SupabaseNetwork.client
                    .postgrest["admin_station_utilization"]
                    .select()
                    .decodeList<AdminStationUtilization>()

                _utilizations.value = utilizationData

                // 2. Yoğun Saatler (Peak Hours) View'unu Çek
                val peakHourData = SupabaseNetwork.client
                    .postgrest["admin_peak_hours"]
                    .select()
                    .decodeList<AdminPeakHour>()

                _peakHours.value = peakHourData

                // 3. Ağ Geneli Toplamları Hesapla
                totalNetworkRevenue.value = utilizationData.sumOf { it.grossRevenue ?: 0.0 }
                totalNetworkPenalties.value = utilizationData.sumOf { it.penaltyRevenue ?: 0.0 }
                totalNetworkSessions.value = utilizationData.sumOf { it.totalSessions ?: 0 }
                totalNetworkCancellations.value = utilizationData.sumOf { it.totalCancellations ?: 0 }

            } catch (e: Exception) {
                println("Dashboard verileri çekilemedi: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
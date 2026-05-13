package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Models.ChargingStation
import com.maherlabbad.EVChargingSystem.Models.FavoriteStation
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteStationData(
    val station: ChargingStation,
    val availableUnits: Int,
    val totalUnits: Int
)

class FavoriteStationsViewModel : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteStationData>>(emptyList())
    val favorites: StateFlow<List<FavoriteStationData>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                // 1. Kullanıcının favori kayıtlarını çek
                val favRecords = SupabaseNetwork.client.postgrest["User_FavoriteStation"]
                    .select { filter { eq("UserID", userId) } }
                    .decodeList<FavoriteStation>()

                val stationIds = favRecords.map { it.stationId }

                if (stationIds.isEmpty()) {
                    _favorites.value = emptyList()
                    return@launch
                }

                // 2. Bu ID'lere ait İstasyon detaylarını çek
                val stations = SupabaseNetwork.client.postgrest["ChargingStation"]
                    .select { filter { isIn("StationID", stationIds) } }
                    .decodeList<ChargingStation>()

                // 3. İstasyonlardaki şarj cihazlarını çek (Müsaitlik durumu için)
                val chargers = SupabaseNetwork.client.postgrest["Charger"]
                    .select { filter { isIn("StationID", stationIds) } }
                    .decodeList<Charger>()

                // 4. Verileri UI için birleştir
                val combinedData = stations.map { station ->
                    val stationChargers = chargers.filter { it.stationID == station.stationID }
                    val total = stationChargers.size
                    val available = stationChargers.count { it.status.equals("Available", ignoreCase = true) }

                    FavoriteStationData(station, available, total)
                }

                _favorites.value = combinedData

            } catch (e: Exception) {
                println("Favoriler yüklenemedi: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // İstasyonu favorilerden çıkarma işlemi
    fun removeFavorite(stationId: String) {
        viewModelScope.launch {
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch
                SupabaseNetwork.client.postgrest["User_FavoriteStation"].delete {
                    filter {
                        eq("UserID", userId)
                        eq("StationID", stationId)
                    }
                }
                // Silme işleminden sonra listeyi yenile
                loadFavorites()
            } catch (e: Exception) {
                println("Favori silinemedi: ${e.localizedMessage}")
            }
        }
    }
}
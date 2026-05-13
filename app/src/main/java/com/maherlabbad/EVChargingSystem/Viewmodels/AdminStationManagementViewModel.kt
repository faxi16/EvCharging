package com.maherlabbad.EVChargingSystem.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Models.ChargingStation
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class MapStationItemAdmin(
    val station: ChargingStation,
    val status: String
)

class AdminViewModel : ViewModel() {
    private val _stations = MutableStateFlow<List<MapStationItemAdmin>>(emptyList())
    val stations: StateFlow<List<MapStationItemAdmin>> = _stations.asStateFlow()

    private var _errMsg = MutableStateFlow("")
    val errMsg: StateFlow<String> = _errMsg.asStateFlow()

    init {
        fetchAllStations()
    }

    fun fetchAllStations() {
        viewModelScope.launch {
            try {
                val allStations = SupabaseNetwork.client.postgrest["ChargingStation"].select().decodeList<ChargingStation>()
                val allChargers = SupabaseNetwork.client.postgrest["Charger"].select().decodeList<Charger>()

                // Şimdi bu istasyonların FR03 kuralına göre renklerini hesapla
                val mapItems = allStations.map { station ->
                    val stationChargers = allChargers.filter { it.stationID == station.stationID }

                    val status = when {

                        // En az 1 cihaz "In Use" ise (ve hiç Available yoksa) SARI
                        stationChargers.any { it.status.equals("In Use", ignoreCase = true) } ->
                            "In Use"

                        // En az 1 cihaz "Available" ise YEŞİL
                        stationChargers.any { it.status.equals("Available", ignoreCase = true) } ->
                            "Available"

                        // Diğer tüm durumlar (Offline vb.) için KIRMIZI
                        else ->
                            "Maintenance"
                    }

                    MapStationItemAdmin(station, status)
                }

                _stations.value = mapItems

            } catch (e: Exception) {
                println("Harita verileri çekilemedi: ${e.message}")
            }
        }
    }

    fun toggleStationStatus(stationId: String, currentStationStateIsAvailable: String) {
        if(currentStationStateIsAvailable == "In Use"){
            _errMsg.value = "Station is in use. Cannot toggle."
            return
        }
        viewModelScope.launch {
            try {
                // Eğer istasyon şu an aktif görünüyorsa, hepsini kapatacağız ("Maintenance").
                // Eğer zaten kapalıysa, hepsini tekrar açacağız ("Available").
                val newChargerStatus = if (currentStationStateIsAvailable == "Available") "Maintenance" else "Available"

                // Filtreye StationID vererek, o istasyona ait TÜM cihazları tek seferde değiştiriyoruz.
                SupabaseNetwork.client
                    .postgrest["Charger"]
                    .update({ set("Status", newChargerStatus) }) {
                        filter { eq("StationID", stationId) }
                    }

                // Cihazların durumu değiştiği için listeyi baştan çek ve UI'ı yenile
                fetchAllStations()

            } catch (e: Exception) {
                _errMsg.value = "Bir hata Oluştu Lütfen Tekrar Deneyiniz..."
                println("Charger durumları güncellenirken hata oluştu: ${e.localizedMessage}")
            }
        }
    }
}
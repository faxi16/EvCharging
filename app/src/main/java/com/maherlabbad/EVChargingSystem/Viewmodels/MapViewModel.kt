package com.maherlabbad.EVChargingSystem.Viewmodels

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maherlabbad.EVChargingSystem.ActiveVehicleSession
import com.maherlabbad.EVChargingSystem.Models.Charger
import com.maherlabbad.EVChargingSystem.Models.ChargingStation
import com.maherlabbad.EVChargingSystem.SupabaseNetwork
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class MapStationItem(
    val station: ChargingStation,
    val statusColor: Float // Google Maps Marker Renk formatı (HUE)
)
class MapViewModel : ViewModel() {

    private val _selectedStationChargers = MutableStateFlow<List<Charger>>(emptyList())
    val selectedStationChargers: StateFlow<List<Charger>> = _selectedStationChargers.asStateFlow()

    // Tüm İstasyonlar (Ham Veri)
    private var allStations = listOf<ChargingStation>()
    private var allChargers = listOf<Charger>()

    // Ekranda gösterilecek filtrelenmiş istasyonlar
    private val _filteredStations = MutableStateFlow<List<MapStationItem>>(emptyList())
    val filteredStations: StateFlow<List<MapStationItem>> = _filteredStations.asStateFlow()

    init {
        observeStationsRealtime()
    }

    fun observeStationsRealtime() {
        viewModelScope.launch {
            // 1. Ekran açıldığında verileri bir kez tazeleyelim
            loadStationsAndFilter()

            // 2. Kanalı oluştur
            val channel = SupabaseNetwork.client.channel("map-updates")

            // 3. Tablo değişikliklerini tanımla (Schema her zaman "public" olmalı)
            val stationChanges = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "ChargingStation"
            }
            val chargerChanges = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "Charger"
            }

            // 4. Değişiklikleri dinlemeye başla (launch içine alıyoruz ki bloklanmasın)
            launch {
                stationChanges.collect { action ->
                    println("🚨 REALTIME: ChargingStation tablosunda değişim saptandı!")
                    loadStationsAndFilter()
                }
            }

            launch {
                chargerChanges.collect { action ->
                    println("🚨 REALTIME: Charger tablosunda değişim saptandı!")
                    loadStationsAndFilter()
                }
            }

            // 5. Kanala abone ol
            channel.subscribe()
        }
    }
    fun loadStationsAndFilter() {
        viewModelScope.launch {
            try {
                allStations = SupabaseNetwork.client.postgrest["ChargingStation"].select().decodeList<ChargingStation>()
                allChargers = SupabaseNetwork.client.postgrest["Charger"].select().decodeList<Charger>()

                val userConnector = ActiveVehicleSession.activeVehicleConnectorType.value

                // Önce araca göre uygun istasyonları filtrele (FR04)
                val compatibleStations = if (userConnector == null) {
                    allStations
                } else {
                    allStations.filter { station ->
                        val chargersInThisStation = allChargers.filter { it.stationID == station.stationID }
                        chargersInThisStation.any { it.connectorType.equals(userConnector, ignoreCase = true) }
                    }
                }

                // Şimdi bu istasyonların FR03 kuralına göre renklerini hesapla
                val mapItems = compatibleStations.map { station ->
                    val stationChargers = allChargers.filter { it.stationID == station.stationID }

                    val colorHue = when {
                        // En az 1 cihaz "Available" ise YEŞİL
                        stationChargers.any { it.status.equals("Available", ignoreCase = true) } ->
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN

                        // En az 1 cihaz "In Use" ise (ve hiç Available yoksa) SARI
                        stationChargers.any { it.status.equals("In Use", ignoreCase = true) } ->
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW

                        // Diğer tüm durumlar (Offline vb.) için KIRMIZI
                        else ->
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                    }

                    MapStationItem(station, colorHue)
                }

                _filteredStations.value = mapItems

            } catch (e: Exception) {
                println("Harita verileri çekilemedi: ${e.message}")
            }
        }
    }

    fun fetchChargersForStation(stationId: String) {
        viewModelScope.launch {
            try {
                // Sadece StationID'si eşleşen Charger'ları getir
                val chargers = SupabaseNetwork.client
                    .postgrest["Charger"]
                    .select {
                        filter {
                            eq("StationID", stationId)
                        }
                    }
                    .decodeList<Charger>()

                _selectedStationChargers.value = chargers
            } catch (e: Exception) {
                println("Priz Hatası: ${e.localizedMessage}")
            }
        }
    }

    // Alt kart kapatıldığında eski prizleri temizle
    fun clearSelectedChargers() {
        _selectedStationChargers.value = emptyList()
    }
}
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

class StationDetailViewModel : ViewModel() {

    // 1. İstasyonun kendi bilgileri (İsim, Konum vb.)
    private val _station = MutableStateFlow<ChargingStation?>(null)
    val station: StateFlow<ChargingStation?> = _station.asStateFlow()

    // 2. O istasyona ait prizlerin (Charger) listesi
    private val _chargers = MutableStateFlow<List<Charger>>(emptyList())
    val chargers: StateFlow<List<Charger>> = _chargers.asStateFlow()

    // Yüklenme durumu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Bu fonksiyonu mevcut loadStationDetails fonksiyonunun İÇİNDE çağır ki sayfa açıldığında durumu kontrol etsin
    private fun checkFavoriteStatus(stationId: String) {
        viewModelScope.launch {
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch
                val fav = SupabaseNetwork.client.postgrest["User_FavoriteStation"]
                    .select {
                        filter {
                            eq("UserID", userId)
                            eq("StationID", stationId)
                        }
                    }.decodeSingleOrNull<FavoriteStation>()

                _isFavorite.value = fav != null
            } catch (e: Exception) {
                println("Favori durumu çekilemedi: ${e.localizedMessage}")
            }
        }
    }

    // Butona basıldığında favoriye ekler veya çıkarır
    fun toggleFavorite(stationId: String) {
        viewModelScope.launch {
            try {
                val userId = SupabaseNetwork.client.auth.currentUserOrNull()?.id ?: return@launch

                if (_isFavorite.value) {
                    // Zaten favoriyse sil
                    SupabaseNetwork.client.postgrest["User_FavoriteStation"].delete {
                        filter {
                            eq("UserID", userId)
                            eq("StationID", stationId)
                        }
                    }
                    _isFavorite.value = false
                } else {
                    // Favori değilse ekle
                    val newFav = FavoriteStation(userId, stationId)
                    SupabaseNetwork.client.postgrest["User_FavoriteStation"].insert(newFav)
                    _isFavorite.value = true
                }
            } catch (e: Exception) {
                println("Favori işlemi başarısız: ${e.localizedMessage}")
            }
        }
    }
    fun loadStationDetails(stationId: String) {

        checkFavoriteStatus(stationId)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Adım: Sadece tıklanan istasyonun bilgilerini çek
                val fetchedStation = SupabaseNetwork.client
                    .postgrest["ChargingStation"]
                    .select {
                        filter {
                            eq("StationID", stationId)
                        }
                    }
                    .decodeSingleOrNull<ChargingStation>()

                _station.value = fetchedStation

                // 2. Adım: Sadece bu istasyona ait prizleri (Charger) çek
                val fetchedChargers = SupabaseNetwork.client
                    .postgrest["Charger"]
                    .select {
                        filter {
                            eq("StationID", stationId)
                        }
                    }
                    .decodeList<Charger>()

                _chargers.value = fetchedChargers

            } catch (e: Exception) {
                println("İstasyon Detay Çekme Hatası: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
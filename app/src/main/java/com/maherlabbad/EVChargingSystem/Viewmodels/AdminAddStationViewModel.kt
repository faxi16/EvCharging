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
import java.util.UUID

// Ekranda oluşturulan cihazları veritabanına gitmeden önce tutacağımız geçici sınıf
data class TempCharger(
    val type: String,
    val powerOutput: Double,
    val unitPrice: Double,
    val connectorType: String
)

class AdminAddStationViewModel : ViewModel() {

    // --- İstasyon (Station) Verileri ---
    var stationName = MutableStateFlow("")
    var location = MutableStateFlow("") // Şemaya göre tek text alanı
    var operatingHours = MutableStateFlow("24/7")

    // --- Cihaz (Charger) Form Verileri ---
    var chargerType = MutableStateFlow("DC")
    var powerOutput = MutableStateFlow("")
    var connectorType = MutableStateFlow("CCS")
    var unitPrice = MutableStateFlow("")

    // Eklenen Cihazların Listesi
    private val _chargersList = MutableStateFlow<List<TempCharger>>(emptyList())
    val chargersList: StateFlow<List<TempCharger>> = _chargersList.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // 1. Cihazı Listeye Ekle Butonu
    fun addChargerToList() {
        val power = powerOutput.value.toDoubleOrNull()
        val price = unitPrice.value.toDoubleOrNull()

        if (power == null || price == null || connectorType.value.isBlank()) {
            _message.value = "Error: Please enter valid numbers for Power and Price."
            _isSuccess.value = false
            return
        }

        val newCharger = TempCharger(
            type = chargerType.value,
            powerOutput = power,
            unitPrice = price,
            connectorType = connectorType.value
        )

        // Listeye ekle
        val currentList = _chargersList.value.toMutableList()
        currentList.add(newCharger)
        _chargersList.value = currentList

        // Formu temizle ki yeni cihaz girebilsin
        powerOutput.value = ""
        unitPrice.value = ""
        _message.value = null
    }

    // 2. Cihazı Listeden Çıkar Butonu
    fun removeCharger(charger: TempCharger) {
        val currentList = _chargersList.value.toMutableList()
        currentList.remove(charger)
        _chargersList.value = currentList
    }

    // 3. İstasyonu ve Tüm Cihazları Veritabanına Kaydet (Submit)
    fun submitStationAndChargers() {
        if (stationName.value.isBlank() || location.value.isBlank()) {
            _message.value = "Error: Station Name and Location are mandatory!"
            _isSuccess.value = false
            return
        }

        if (_chargersList.value.isEmpty()) {
            _message.value = "Error: You must add at least 1 charger to the station."
            _isSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _message.value = null
            try {
                // 1. İstasyonu Ekle
                val newStationId = UUID.randomUUID().toString()

                val stationData = ChargingStation(
                    stationID = newStationId,
                    name = stationName.value,
                    location = location.value,
                    operatingHours = operatingHours.value
                )
                SupabaseNetwork.client.postgrest["ChargingStation"].insert(stationData)

                // 2. Listeki Tüm Cihazları Döngüyle Ekle
                _chargersList.value.forEach { tempCharger ->

                    val chargerData = Charger(
                        chargerID = UUID.randomUUID().toString(),
                        type = tempCharger.type,
                        powerOutput = tempCharger.powerOutput,
                        unitPrice = tempCharger.unitPrice,
                        connectorType = tempCharger.connectorType,
                        status = "Available",
                        stationID = newStationId
                    )
                    SupabaseNetwork.client.postgrest["Charger"].insert(chargerData)
                }

                _isSuccess.value = true
                _message.value = "Station and ${_chargersList.value.size} chargers added successfully!"

                // Her şeyi sıfırla
                stationName.value = ""
                location.value = ""
                _chargersList.value = emptyList()

            } catch (e: Exception) {
                _message.value = "Failed to add data: ${e.localizedMessage}"
                _isSuccess.value = false
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
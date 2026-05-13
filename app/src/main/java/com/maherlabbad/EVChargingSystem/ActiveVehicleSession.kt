package com.maherlabbad.EVChargingSystem

import kotlinx.coroutines.flow.MutableStateFlow

object ActiveVehicleSession {
    val activeVehicleConnectorType = MutableStateFlow<String?>(null)
    val activeVehicleId = MutableStateFlow<String?>(null)

    fun setActiveVehicle(id: String, connectorType: String) {
        activeVehicleId.value = id
        activeVehicleConnectorType.value = connectorType
    }
}
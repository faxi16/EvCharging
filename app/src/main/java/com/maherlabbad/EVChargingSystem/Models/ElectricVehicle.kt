package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElectricVehicle(
    @SerialName("PlateNumber") val plateNumber: String,
    @SerialName("Brand") val brand: String,
    @SerialName("Model") val model: String,
    @SerialName("BatteryCapacity") val batteryCapacity: Double,
    @SerialName("ConnectorType") val connectorType: String,
    @SerialName("UserID") val userId: String
)

package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Charger(
    @SerialName("ChargerID") val chargerID: String,
    @SerialName("Type") val type: String,
    @SerialName("PowerOutput") val powerOutput: Double,
    @SerialName("UnitPrice") val unitPrice: Double,
    @SerialName("ConnectorType") val connectorType: String,
    @SerialName("Status") val status: String,
    @SerialName("StationID") val stationID: String
)
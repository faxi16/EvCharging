package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargingStation(
    @SerialName("StationID") val stationID: String,
    @SerialName("Name") val name: String,
    @SerialName("Location") val location: String,
    @SerialName("OperatingHours") val operatingHours: String
)



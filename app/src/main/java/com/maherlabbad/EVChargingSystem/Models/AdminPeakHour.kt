package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminPeakHour(
    @SerialName("StationID") val stationID: String? = null,
    @SerialName("StationName") val stationName: String? = null,
    @SerialName("usage_hour") val usageHour: Double? = 0.0,
    @SerialName("total_reservations") val totalReservations: Long? = 0
)

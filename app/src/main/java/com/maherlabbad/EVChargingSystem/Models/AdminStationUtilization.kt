package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminStationUtilization(
    @SerialName("StationID") val stationID: String? = null,
    @SerialName("StationName") val stationName: String? = null,
    @SerialName("TotalSessions") val totalSessions: Int? = 0,
    @SerialName("ChargingRevenue") val chargingRevenue: Double? = 0.0,
    @SerialName("PenaltyRevenue") val penaltyRevenue: Double? = 0.0,
    @SerialName("GrossRevenue") val grossRevenue: Double? = 0.0,
    @SerialName("TotalCancellations") val totalCancellations: Int? = 0
)

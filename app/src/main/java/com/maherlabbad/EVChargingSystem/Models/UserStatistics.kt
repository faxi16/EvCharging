package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserStatistics(
    @SerialName("UserID") val userID: String,
    @SerialName("TotalSessions") val totalSessions: Int,
    @SerialName("TotalEnergyConsumed_kWh") val totalEnergy: Double,
    @SerialName("TotalSpent_TL") val totalSpent: Double
)
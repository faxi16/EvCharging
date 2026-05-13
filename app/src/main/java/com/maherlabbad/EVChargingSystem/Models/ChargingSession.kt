package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargingSession(
    @SerialName("ReservationID") val reservationID: String,
    @SerialName("SessionID") val sessionID: String,
    @SerialName("EnergyConsumed") val energyConsumed: Double,
    @SerialName("FinalCost") val finalCost: Double,
    @SerialName("ConnectionStatus") val connectionStatus: String
)

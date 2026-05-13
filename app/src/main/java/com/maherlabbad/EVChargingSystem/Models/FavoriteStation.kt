package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteStation(
    @SerialName("UserID") val userId: String,
    @SerialName("StationID") val stationId: String
)

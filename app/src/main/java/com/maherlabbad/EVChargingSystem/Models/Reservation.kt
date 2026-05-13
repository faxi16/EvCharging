package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    @SerialName("ReservationID") val reservationID: String,
    @SerialName("Date") val date: String, // SQL Date formatı yyyy-MM-dd
    @SerialName("StartTime") val startTime: String,
    @SerialName("EndTime") val endTime: String,
    @SerialName("Status") val status: String = "Pending",
    @SerialName("UserID") val userID: String,
    @SerialName("ChargerID") val chargerID: String
)
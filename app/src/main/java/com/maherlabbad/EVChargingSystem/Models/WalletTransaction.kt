package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletTransaction(
    @SerialName("TransactionID") val transactionID: String,
    @SerialName("UserID") val userID: String,
    @SerialName("Amount") val amount: Double,
    @SerialName("TransactionType") val type: String,
    @SerialName("Timestamp") val timestamp: String,
    @SerialName("Description") val description: String? = null,
    @SerialName("ReservationID") val reservationId: String? = null
)
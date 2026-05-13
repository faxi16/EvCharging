package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    @SerialName("UserID") val userID: String,
    @SerialName("Balance") val balance: Double,
    @SerialName("MinBalanceRequirement") val minBalance: Double = 50.0
)

package com.maherlabbad.EVChargingSystem.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("UserID") val userId: String, // UUID
    @SerialName("Name") val name: String,
    @SerialName("Surname") val surname: String,
    @SerialName("Email") val email: String,
    @SerialName("Role") val role: String? = null
)
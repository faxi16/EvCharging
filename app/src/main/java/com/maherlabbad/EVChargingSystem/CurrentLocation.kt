package com.maherlabbad.EVChargingSystem

import kotlinx.coroutines.flow.MutableStateFlow

object CurrentLocation {
    val latitude = MutableStateFlow<Double>(0.0)
    val longitude = MutableStateFlow<Double>(0.0)

    fun setLocation(lat: Double, lng: Double) {
        latitude.value = lat
        longitude.value = lng
    }
}

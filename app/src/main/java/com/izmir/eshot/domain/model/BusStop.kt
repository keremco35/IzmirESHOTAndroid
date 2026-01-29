package com.izmir.eshot.domain.model

/**
 * Domain model representing a bus stop.
 */
data class BusStop(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val routes: List<String> = emptyList() // List of bus routes passing through this stop
)

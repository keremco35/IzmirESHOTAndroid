package com.izmir.eshot.domain.model

/**
 * Domain model representing an approaching bus.
 */
data class ApproachingBus(
    val routeNumber: String,
    val routeName: String,
    val direction: String,
    val vehicleId: String,
    val departureTime: String,
    val approachingTime: String,
    val latitude: Double?,
    val longitude: Double?,
    val speed: Int?
)

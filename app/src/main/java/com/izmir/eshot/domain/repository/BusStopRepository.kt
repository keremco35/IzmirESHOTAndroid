package com.izmir.eshot.domain.repository

import com.izmir.eshot.domain.model.ApproachingBus
import com.izmir.eshot.domain.model.BusStop

/**
 * Repository interface for bus stop operations.
 * Defines the contract for data access.
 */
interface BusStopRepository {
    
    /**
     * Fetches all bus stops from the İzmir ESHOT data source.
     * @return List of bus stops with their details
     * @throws Exception if network or parsing error occurs
     */
    suspend fun getBusStops(): List<BusStop>
    
    /**
     * Fetches approaching buses for a specific stop.
     * @param stopId The ID of the bus stop
     * @return List of approaching buses
     * @throws Exception if network error occurs
     */
    suspend fun getApproachingBuses(stopId: Int): List<ApproachingBus>
}

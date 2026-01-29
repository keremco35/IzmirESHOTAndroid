package com.izmir.eshot.presentation.state

import com.izmir.eshot.domain.model.ApproachingBus
import com.izmir.eshot.domain.model.BusStop

/**
 * Sealed class representing the UI state for bus stops screen.
 */
sealed class BusStopsUiState {
    data object Loading : BusStopsUiState()
    
    data class Success(
        val busStops: List<BusStop>,
        val selectedStop: BusStop? = null,
        val approachingBuses: List<ApproachingBus> = emptyList(),
        val isLoadingApproachingBuses: Boolean = false
    ) : BusStopsUiState()
    
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : BusStopsUiState()
}

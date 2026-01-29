package com.izmir.eshot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmir.eshot.domain.model.BusStop
import com.izmir.eshot.domain.repository.BusStopRepository
import com.izmir.eshot.presentation.state.BusStopsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing bus stops data and UI state.
 */
@HiltViewModel
class BusStopsViewModel @Inject constructor(
    private val repository: BusStopRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<BusStopsUiState>(BusStopsUiState.Loading)
    val uiState: StateFlow<BusStopsUiState> = _uiState.asStateFlow()
    
    init {
        loadBusStops()
    }
    
    /**
     * Loads all bus stops from the repository.
     */
    fun loadBusStops() {
        viewModelScope.launch {
            _uiState.value = BusStopsUiState.Loading
            try {
                val busStops = repository.getBusStops()
                _uiState.value = BusStopsUiState.Success(busStops = busStops)
            } catch (e: Exception) {
                _uiState.value = BusStopsUiState.Error(
                    message = e.localizedMessage ?: "Duraklar yüklenirken hata oluştu",
                    exception = e
                )
            }
        }
    }
    
    /**
     * Selects a bus stop and loads approaching buses for it.
     */
    fun selectBusStop(busStop: BusStop) {
        val currentState = _uiState.value
        if (currentState is BusStopsUiState.Success) {
            _uiState.update {
                currentState.copy(
                    selectedStop = busStop,
                    approachingBuses = emptyList(),
                    isLoadingApproachingBuses = true
                )
            }
            
            viewModelScope.launch {
                try {
                    val approachingBuses = repository.getApproachingBuses(busStop.id)
                    _uiState.update {
                        if (it is BusStopsUiState.Success) {
                            it.copy(
                                approachingBuses = approachingBuses,
                                isLoadingApproachingBuses = false
                            )
                        } else it
                    }
                } catch (e: Exception) {
                    // Log error for debugging
                    e.printStackTrace()
                    _uiState.update {
                        if (it is BusStopsUiState.Success) {
                            it.copy(
                                approachingBuses = emptyList(),
                                isLoadingApproachingBuses = false
                            )
                        } else it
                    }
                }
            }
        }
    }
    
    /**
     * Clears the selected bus stop.
     */
    fun clearSelection() {
        val currentState = _uiState.value
        if (currentState is BusStopsUiState.Success) {
            _uiState.update {
                currentState.copy(
                    selectedStop = null,
                    approachingBuses = emptyList(),
                    isLoadingApproachingBuses = false
                )
            }
        }
    }
    
    /**
     * Retry loading bus stops after an error.
     */
    fun retry() {
        loadBusStops()
    }
}

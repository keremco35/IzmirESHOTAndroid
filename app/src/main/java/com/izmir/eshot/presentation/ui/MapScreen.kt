package com.izmir.eshot.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.izmir.eshot.domain.model.BusStop
import com.izmir.eshot.presentation.state.BusStopsUiState
import com.izmir.eshot.presentation.ui.components.BusStopBottomSheet
import com.izmir.eshot.presentation.ui.components.ErrorScreen
import com.izmir.eshot.presentation.ui.components.LoadingAnimation
import com.izmir.eshot.presentation.viewmodel.BusStopsViewModel
import kotlinx.coroutines.launch

// Default camera position: İzmir city center (Konak)
private val IZMIR_CENTER = LatLng(38.4192, 27.1287)
private const val DEFAULT_ZOOM = 12f

/**
 * Main map screen displaying bus stops on Google Maps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: BusStopsViewModel = hiltViewModel(),
    hasLocationPermission: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(IZMIR_CENTER, DEFAULT_ZOOM)
    }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    
    // Map properties
    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapType = MapType.NORMAL
        )
    }
    
    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            compassEnabled = true,
            mapToolbarEnabled = true
        )
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is BusStopsUiState.Loading -> {
                LoadingAnimation()
            }
            
            is BusStopsUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.retry() }
                )
            }
            
            is BusStopsUiState.Success -> {
                // Handle bottom sheet visibility
                LaunchedEffect(state.selectedStop) {
                    if (state.selectedStop != null) {
                        showBottomSheet = true
                        try {
                            sheetState.show()
                        } catch (e: Exception) {
                            // Sheet state error, ignore
                        }
                    }
                }
                
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    onMapLoaded = {
                        // Optionally animate to fit all markers
                    }
                ) {
                    // Display bus stop markers
                    state.busStops.forEach { busStop ->
                        BusStopMarker(
                            busStop = busStop,
                            onClick = {
                                viewModel.selectBusStop(busStop)
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(busStop.latitude, busStop.longitude),
                                            16f
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
                
                // Bottom sheet for selected bus stop
                if (showBottomSheet && state.selectedStop != null) {
                    BusStopBottomSheet(
                        busStop = state.selectedStop,
                        approachingBuses = state.approachingBuses,
                        isLoadingApproachingBuses = state.isLoadingApproachingBuses,
                        sheetState = sheetState,
                        onDismiss = {
                            showBottomSheet = false
                            viewModel.clearSelection()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual bus stop marker on the map.
 */
@Composable
private fun BusStopMarker(
    busStop: BusStop,
    onClick: () -> Unit
) {
    Marker(
        state = MarkerState(position = LatLng(busStop.latitude, busStop.longitude)),
        title = busStop.name,
        snippet = "Durak No: ${busStop.id}",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
        onClick = {
            onClick()
            true // Consume the click
        }
    )
}

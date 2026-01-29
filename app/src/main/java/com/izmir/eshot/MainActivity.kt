package com.izmir.eshot

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.izmir.eshot.presentation.ui.MapScreen
import com.izmir.eshot.presentation.ui.theme.IzmirEshotTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity hosting the map screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            IzmirEshotTheme {
                MainContent()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainContent() {
    // Location permission state
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Request permissions when the app starts
    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }
    
    val hasLocationPermission = locationPermissionState.allPermissionsGranted
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        MapScreen(
            hasLocationPermission = hasLocationPermission,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

package com.herbarium.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.herbarium.viewmodel.AddPlantViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    viewModel: AddPlantViewModel,
    modifier: Modifier = Modifier
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status) {
        when (locationPermissionState.status) {
            PermissionStatus.Granted -> viewModel.getCurrentLocation()
            is PermissionStatus.Denied -> {
                // nothing
            }
        }
    }

    if (locationPermissionState.status.shouldShowRationale) {
        Text("Location permission is needed to tag plants")
    }

    Button(
        onClick = { locationPermissionState.launchPermissionRequest() },
        modifier = modifier
    ) {
        Text("Get Current Location")
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location"
        )
    }
}
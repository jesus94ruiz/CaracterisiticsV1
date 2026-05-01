package com.jera.caracterisiticsv1.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.viewmodels.MapViewModel

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.updateLocationPermission(granted)
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFF1A1A1A),
                contentColor = Color(0xFFFF6B00)
            ) {
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color(0xFFFF6B00))
                        )
                    },
                    selected = true,
                    onClick = {},
                    selectedContentColor = Color(0xFFFF6B00),
                    unselectedContentColor = Color.Gray
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "Camara",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )
                    },
                    selected = false,
                    onClick = { navController.navigate(AppScreens.CameraScreen.route) },
                    selectedContentColor = Color(0xFFFF6B00),
                    unselectedContentColor = Color.Gray
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.gallery),
                            contentDescription = "Galeria",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )
                    },
                    selected = false,
                    onClick = { navController.navigate(AppScreens.GalleryScreen.route) },
                    selectedContentColor = Color(0xFFFF6B00),
                    unselectedContentColor = Color.Gray
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.car_in_garage),
                            contentDescription = "Garaje",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )
                    },
                    selected = false,
                    onClick = { navController.navigate(AppScreens.GarageScreen.route) },
                    selectedContentColor = Color(0xFFFF6B00),
                    unselectedContentColor = Color.Gray
                )
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Configuracion",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )
                    },
                    selected = false,
                    onClick = { navController.navigate(AppScreens.SettingsScreen.route) },
                    selectedContentColor = Color(0xFFFF6B00),
                    unselectedContentColor = Color.Gray
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoadingLocation -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFF6B00)
                    )
                }

                uiState.currentLocation != null -> {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(uiState.currentLocation!!, 15f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        contentPadding = PaddingValues(bottom = 56.dp),
                        properties = MapProperties(
                            isMyLocationEnabled = uiState.hasLocationPermission,
                            isBuildingEnabled = false,
                            isIndoorEnabled = false,
                            mapStyleOptions = MapStyleOptions(getMapStyle())
                        ),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = true,
                            zoomControlsEnabled = false,
                            compassEnabled = true,
                            mapToolbarEnabled = false,
                            tiltGesturesEnabled = false,
                            indoorLevelPickerEnabled = false
                        )
                    ) {
                        viewModel.getCarsWithLocation().forEach { (location, car) ->
                            Marker(
                                state = MarkerState(position = location),
                                title = "${car.make_name} ${car.model_name}",
                                snippet = car.years
                            )
                        }
                    }
                }

                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFF6B00)
                    )
                }
            }
        }
    }
}

private fun getMapStyle(): String = """
[
  { "elementType": "geometry", "stylers": [{ "color": "#1a1a2e" }] },
  { "elementType": "labels.text.fill", "stylers": [{ "color": "#8ec3b9" }] },
  { "elementType": "labels.text.stroke", "stylers": [{ "color": "#1a3646" }] },
  { "featureType": "administrative.country", "elementType": "geometry.stroke", "stylers": [{ "color": "#4b6878" }] },
  { "featureType": "administrative.land_parcel", "elementType": "labels.text.fill", "stylers": [{ "color": "#64779e" }] },
  { "featureType": "administrative.province", "elementType": "geometry.stroke", "stylers": [{ "color": "#4b6878" }] },
  { "featureType": "landscape.man_made", "elementType": "geometry.stroke", "stylers": [{ "color": "#334e87" }] },
  { "featureType": "landscape.natural", "elementType": "geometry", "stylers": [{ "color": "#023e58" }] },
  { "featureType": "poi", "stylers": [{ "visibility": "off" }] },
  { "featureType": "road", "elementType": "geometry", "stylers": [{ "color": "#304a7d" }] },
  { "featureType": "road", "elementType": "labels.text.fill", "stylers": [{ "color": "#98a5be" }] },
  { "featureType": "road", "elementType": "labels.text.stroke", "stylers": [{ "color": "#1d2c4d" }] },
  { "featureType": "road.highway", "elementType": "geometry", "stylers": [{ "color": "#2c6675" }] },
  { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [{ "color": "#255763" }] },
  { "featureType": "road.highway", "elementType": "labels.text.fill", "stylers": [{ "color": "#b0d5ce" }] },
  { "featureType": "road.highway", "elementType": "labels.text.stroke", "stylers": [{ "color": "#023747" }] },
  { "featureType": "transit", "stylers": [{ "visibility": "off" }] },
  { "featureType": "water", "elementType": "geometry", "stylers": [{ "color": "#0e1626" }] },
  { "featureType": "water", "elementType": "labels.text.fill", "stylers": [{ "color": "#4e6d70" }] }
]
""".trimIndent()

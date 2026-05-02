package com.jera.caracterisiticsv1.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.MapViewModel

// ─── Colores usados en MapScreen ─────────────────────────────────────────────
// BottomNav fondo:          CyberDark     (#110015)
// BottomNav ítem activo:    CyberYellow   (#fff04c)
// BottomNav ítem inactivo:  SurfaceLight  (#4d3352)
// Loading indicator:        AccentPrimary (#ff7037)
// Borde overlay:            AccentPrimary (#ff7037) alpha
// Mapa — estilo cyberpunk futurista (ver getMapStyle)
// ─────────────────────────────────────────────────────────────────────────────

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
        backgroundColor = CyberDark,
        bottomBar = {
            // ── BottomNav con estilo cyberpunk ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor)
                    .border(
                        width = 1.dp,
                        color = AccentPrimary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
            ) {
                BottomNavigation(
                    backgroundColor = SurfaceColor,
                    contentColor = CyberYellow,
                    elevation = 0.dp
                ) {
                    // MAP (activo)
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = "Mapa",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(CyberYellow)
                            )
                        },
                        selected = true,
                        onClick = {},
                        selectedContentColor = CyberYellow,
                        unselectedContentColor = SurfaceLight
                    )
                    // CAMERA
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Camara",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(SurfaceLight)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate(AppScreens.CameraScreen.route) },
                        selectedContentColor = CyberYellow,
                        unselectedContentColor = SurfaceLight
                    )
                    // GALLERY
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.gallery),
                                contentDescription = "Galeria",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(SurfaceLight)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate(AppScreens.GalleryScreen.route) },
                        selectedContentColor = CyberYellow,
                        unselectedContentColor = SurfaceLight
                    )
                    // GARAGE
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.car_in_garage),
                                contentDescription = "Garaje",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(SurfaceLight)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate(AppScreens.GarageScreen.route) },
                        selectedContentColor = CyberYellow,
                        unselectedContentColor = SurfaceLight
                    )
                    // SETTINGS
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.settings),
                                contentDescription = "Configuracion",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(SurfaceLight)
                            )
                        },
                        selected = false,
                        onClick = { navController.navigate(AppScreens.SettingsScreen.route) },
                        selectedContentColor = CyberYellow,
                        unselectedContentColor = SurfaceLight
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CyberDark)
        ) {
            when {
                uiState.isLoadingLocation -> {
                    // Pantalla de carga estilo cyberpunk
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AccentPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "LOCALIZANDO...",
                            color = CyberYellow,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 3.sp
                        )
                    }
                }

                uiState.currentLocation != null -> {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(uiState.currentLocation!!, 15f)
                    }

                    // Mapa con estilo cyberpunk
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = uiState.hasLocationPermission,
                            isBuildingEnabled = false,
                            isIndoorEnabled = false,
                            mapStyleOptions = MapStyleOptions(getCyberpunkMapStyle())
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

                    // Overlay: etiqueta HUD en esquina superior izquierda
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberDark.copy(alpha = 0.8f))
                            .border(
                                1.dp,
                                AccentPrimary.copy(alpha = 0.6f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "// MINI MAP",
                            color = CyberYellow,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AccentPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "INICIALIZANDO GPS...",
                            color = NeonAmber,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

// ─── Estilo de mapa cyberpunk futurista ───────────────────────────────────────
// Paleta de referencia:
//   Fondo base:     #110015  (CyberDark)
//   Superficie:     #1a0020  (SurfaceColor)
//   Carreteras:     #ff7037  (AccentPrimary)   — neon orange
//   Arteriales:     #a3306f  (NeonPurple)       — neon magenta
//   Autopistas:     #fff04c  (CyberYellow)      — neon yellow
//   Autopista stroke: #ff9b3e (AccentOrange)
//   Labels:         #ffffff  (CyberWhite)
//   Labels stroke:  #110015  (CyberDark)
//   Agua:           #0d000f  (casi negro morado)
//   Natural:        #150018  (oscuro morado)
//   Man-made:       #1a0022
//   Lime accent:    #b8d14b  (CyberLime) — usado en local roads
// ─────────────────────────────────────────────────────────────────────────────
private fun getCyberpunkMapStyle(): String = """
[
  { "elementType": "geometry",
    "stylers": [{ "color": "#110015" }] },
  { "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ffffff" }] },
  { "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#110015" }] },

  { "featureType": "administrative",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#a3306f" }, { "weight": 1 }] },
  { "featureType": "administrative.country",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#75108b" }, { "weight": 1.5 }] },
  { "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#fff04c" }] },
  { "featureType": "administrative.locality",
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#110015" }] },
  { "featureType": "administrative.neighborhood",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ffc545" }] },

  { "featureType": "landscape.man_made",
    "elementType": "geometry",
    "stylers": [{ "color": "#1a0022" }] },
  { "featureType": "landscape.man_made",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#2a0035" }] },
  { "featureType": "landscape.natural",
    "elementType": "geometry",
    "stylers": [{ "color": "#150018" }] },
  { "featureType": "landscape.natural.terrain",
    "elementType": "geometry",
    "stylers": [{ "color": "#0e0012" }] },

  { "featureType": "poi",
    "elementType": "geometry",
    "stylers": [{ "color": "#1a0022" }] },
  { "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#a3306f" }] },
  { "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [{ "color": "#0e0012" }] },
  { "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#b8d14b" }] },

  { "featureType": "road",
    "elementType": "geometry",
    "stylers": [{ "color": "#2a0035" }] },
  { "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#110015" }] },
  { "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ffc545" }] },
  { "featureType": "road",
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#110015" }] },

  { "featureType": "road.local",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#280030" }] },
  { "featureType": "road.local",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#b8d14b" }, { "weight": 0.5 }] },
  { "featureType": "road.local",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#b8d14b" }] },

  { "featureType": "road.arterial",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#3a0050" }] },
  { "featureType": "road.arterial",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#a3306f" }, { "weight": 1 }] },
  { "featureType": "road.arterial",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ff9b3e" }] },

  { "featureType": "road.highway",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#4d0060" }] },
  { "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#fff04c" }, { "weight": 1.5 }] },
  { "featureType": "road.highway",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#fff04c" }] },
  { "featureType": "road.highway",
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#110015" }] },

  { "featureType": "road.highway.controlled_access",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#5a0070" }] },
  { "featureType": "road.highway.controlled_access",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#ff7037" }, { "weight": 2 }] },
  { "featureType": "road.highway.controlled_access",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ff7037" }] },

  { "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{ "color": "#1a0020" }] },
  { "featureType": "transit.line",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#75108b" }] },
  { "featureType": "transit.line",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#d15053" }, { "weight": 1 }] },
  { "featureType": "transit.station",
    "elementType": "geometry",
    "stylers": [{ "color": "#2a0035" }] },
  { "featureType": "transit.station",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#ffc545" }] },

  { "featureType": "water",
    "elementType": "geometry",
    "stylers": [{ "color": "#0d000f" }] },
  { "featureType": "water",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#75108b" }, { "weight": 1 }] },
  { "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#a3306f" }] }
]
""".trimIndent()

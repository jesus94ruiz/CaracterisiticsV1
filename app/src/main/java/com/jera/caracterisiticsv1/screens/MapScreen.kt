package com.jera.caracterisiticsv1.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.components.FloatingNavHub
import com.jera.caracterisiticsv1.ui.components.MissionsCompactCard
import com.jera.caracterisiticsv1.ui.components.MissionsExpandedOverlay
import com.jera.caracterisiticsv1.ui.components.UserInfoPanel
import com.jera.caracterisiticsv1.ui.components.placeholderMissions
import com.jera.caracterisiticsv1.ui.components.placeholderUser
import com.jera.caracterisiticsv1.ui.theme.*
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

    var missionsExpanded by remember { mutableStateOf(false) }

    // ── Contenedor raíz — sin Scaffold, sin BottomBar ─────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark)
    ) {

        // ── Capa 1: Contenido principal (mapa / estado de carga) ──────────────
        when {
            uiState.isLoadingLocation -> {
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

        // ── Capa 2: HUD superpuesto ───────────────────────────────────────────

        // ── Arriba izquierda: etiqueta // MINI MAP ────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(CyberDark.copy(alpha = 0.85f))
                .border(
                    1.dp,
                    AccentPrimary.copy(alpha = 0.55f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
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

        // ── Arriba derecha: UserInfoPanel ─────────────────────────────────────
        UserInfoPanel(
            user = placeholderUser,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 48.dp)
        )

        // ── Abajo izquierda: MissionsCompactCard ──────────────────────────────
        MissionsCompactCard(
            missions = placeholderMissions,
            onExpand = { missionsExpanded = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 24.dp)
        )

        // ── Overlay expandido de misiones (capa superior) ─────────────────────
        if (missionsExpanded) {
            MissionsExpandedOverlay(
                missions = placeholderMissions,
                onDismiss = { missionsExpanded = false },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Abajo derecha: FloatingNavHub ─────────────────────────────────────
        FloatingNavHub(
            onCameraClick  = { navController.navigate(AppScreens.CameraScreen.route) },
            onGarageClick  = { navController.navigate(AppScreens.GarageScreen.route) },
            onGalleryClick = { navController.navigate(AppScreens.GalleryScreen.route) },
            onSettingsClick = { navController.navigate(AppScreens.SettingsScreen.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
        )
    }
}

// ─── Estilo de mapa cyberpunk futurista ───────────────────────────────────────
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
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#0d000f" }] },
  { "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#75108b" }] }
]
""".trimIndent()

package com.jera.caracterisiticsv1.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import kotlinx.coroutines.launch

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

    // CameraPositionState hoisted para poder animar desde el botón de centrar
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            uiState.currentLocation ?: com.google.android.gms.maps.model.LatLng(40.416775, -3.703790),
            15f
        )
    }

    // Actualizar la cámara cuando llegue la ubicación por primera vez
    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 15f)
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()

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
                        myLocationButtonEnabled = false,   // usamos botón personalizado
                        zoomControlsEnabled = false,
                        compassEnabled = true,
                        mapToolbarEnabled = false,
                        tiltGesturesEnabled = false,
                        indoorLevelPickerEnabled = false
                    ),
                    // Empuja la marca de agua de Google hacia arriba,
                    // dejándola justo encima del panel de misiones
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        bottom = 136.dp
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

        // ── Debajo del MINI MAP: botón de centrar en mi ubicación ─────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 108.dp)   // justo debajo del MINI MAP (~48+36+24)
                .size(36.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(CyberDark.copy(alpha = 0.90f))
                .border(1.dp, AccentPrimary.copy(alpha = 0.7f), CircleShape)
                .clickable {
                    uiState.currentLocation?.let { loc ->
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(loc, 15f)
                            )
                        }
                    }
                }
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Centrar mapa",
                tint = CyberYellow,
                modifier = Modifier.size(18.dp)
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
            onCameraClick   = { navController.navigate(AppScreens.CameraScreen.route) },
            onGarageClick   = { navController.navigate(AppScreens.GarageScreen.route) },
            onGalleryClick  = { navController.navigate(AppScreens.GalleryScreen.route) },
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
  { "featureType": "road.highway",
    "elementType": "geometry.fill",
    "stylers": [{ "color": "#4a0070" }] },
  { "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#c040e0" }, { "weight": 1.5 }] },

  { "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{ "color": "#1a0022" }] },
  { "featureType": "transit.station",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#c040e0" }] },

  { "featureType": "water",
    "elementType": "geometry",
    "stylers": [{ "color": "#0a000f" }] },
  { "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#4a0070" }] }
]
"""

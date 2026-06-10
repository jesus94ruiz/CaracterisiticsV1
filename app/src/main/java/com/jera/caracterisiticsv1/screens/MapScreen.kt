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
import androidx.compose.ui.graphics.Brush
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
import com.jera.caracterisiticsv1.ui.components.AchievementToast
import com.jera.caracterisiticsv1.ui.components.FloatingNavHub
import com.jera.caracterisiticsv1.ui.components.LevelUpOverlay
import com.jera.caracterisiticsv1.ui.components.MissionsCompactCard
import com.jera.caracterisiticsv1.ui.components.MissionsExpandedOverlay
import com.jera.caracterisiticsv1.ui.components.UserInfo
import com.jera.caracterisiticsv1.ui.components.UserInfoPanel
import com.jera.caracterisiticsv1.ui.components.XpGainedToast
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.MapViewModel
import com.jera.caracterisiticsv1.viewmodels.MissionsViewModel
import com.jera.caracterisiticsv1.viewmodels.ProfileViewModel
import com.jera.caracterisiticsv1.viewmodels.SocialMapViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    missionsViewModel: MissionsViewModel = hiltViewModel(),
    socialMapViewModel: SocialMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val xpGainedEvent by profileViewModel.xpGainedEvent.collectAsState()
    val levelUpEvent by profileViewModel.levelUpEvent.collectAsState()
    val pendingAchievements by profileViewModel.pendingAchievements.collectAsState()
    val missionsUiState by missionsViewModel.uiState.collectAsState()
    val socialCars by socialMapViewModel.capturedCars.collectAsState()

    val userInfo = UserInfo(
        name = profileState.profile.username,
        level = profileState.profile.level,
        currentXp = profileState.profile.currentXp,
        maxXp = profileState.xpForNextLevel
    )

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
                    // Marcadores propios
                    viewModel.getCarsWithLocation().forEach { (location, car) ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "${car.make_name} ${car.model_name}",
                            snippet = car.years
                        )
                    }
                    // Marcadores sociales (coches de otros usuarios)
                    socialCars.forEach { car ->
                        val pos = com.google.android.gms.maps.model.LatLng(car.latitude, car.longitude)
                        Marker(
                            state = MarkerState(position = pos),
                            title = "${car.makeName} ${car.modelName}",
                            snippet = "@${car.username} · ${car.years}"
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
                .padding(start = 12.dp, top = 24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CyberOrange.copy(alpha = 0.82f),
                            CyberOrangeDark.copy(alpha = 0.82f),
                            CyberPurple.copy(alpha = 0.82f)
                        )
                    )
                )
                .border(1.dp, CyberWhite.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "// MINI MAP",
                color = CyberWhite,
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
                .padding(start = 16.dp, top = 84.dp)
                .size(36.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyberOrange.copy(alpha = 0.82f),
                            CyberOrangeDark.copy(alpha = 0.82f),
                            CyberPurple.copy(alpha = 0.82f)
                        )
                    )
                )
                .border(1.dp, CyberWhite.copy(alpha = 0.3f), CircleShape)
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
                tint = CyberWhite,
                modifier = Modifier.size(18.dp)
            )
        }

        // ── Arriba derecha: UserInfoPanel ─────────────────────────────────────
        UserInfoPanel(
            user = userInfo,
            onClick = { navController.navigate(AppScreens.ProfileScreen.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 24.dp)
        )

        // ── Debajo de UserInfoPanel: botón Amigos ─────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CyberOrange.copy(alpha = 0.82f),
                            CyberOrangeDark.copy(alpha = 0.82f),
                            CyberPurple.copy(alpha = 0.82f)
                        )
                    )
                )
                .border(1.dp, CyberWhite.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .clickable { navController.navigate(AppScreens.FriendsScreen.route) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            androidx.compose.material3.Text(
                text = "👥 AMIGOS",
                color = CyberWhite,
                fontFamily = Poppins,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
        }

        // ── Abajo izquierda: MissionsCompactCard ──────────────────────────────
        MissionsCompactCard(
            missions = missionsUiState.missions,
            onExpand = { missionsExpanded = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 24.dp)
        )

        // ── Abajo derecha: FloatingNavHub ─────────────────────────────────────
        FloatingNavHub(
            onCameraClick      = { navController.navigate(AppScreens.CameraScreen.route) },
            onGarageClick      = { navController.navigate(AppScreens.GarageScreen.route) },
            onGalleryClick     = { navController.navigate(AppScreens.GalleryScreen.route) },
            onSettingsClick    = { navController.navigate(AppScreens.SettingsScreen.route) },
            onLeaderboardClick = { navController.navigate(AppScreens.LeaderboardScreen.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
        )

        // ── Overlay expandido de misiones ─────────────────────────────────────
        if (missionsExpanded) {
            MissionsExpandedOverlay(
                missions = missionsUiState.missions,
                onDismiss = { missionsExpanded = false },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Overlays de feedback XP (encima de todo) ──────────────────────────

        // XP Toast — arriba centro
        xpGainedEvent?.let { xp ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                XpGainedToast(
                    xp = xp,
                    onDismiss = { profileViewModel.consumeXpGainedEvent() }
                )
            }
        }

        // Achievement Toast — abajo centro (por encima del nav hub)
        pendingAchievements.firstOrNull()?.let { ach ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp)
            ) {
                AchievementToast(
                    icon = ach.icon,
                    title = ach.title,
                    description = ach.description,
                    onDismiss = { profileViewModel.consumeFirstAchievement() }
                )
            }
        }

        // Level Up Overlay — pantalla completa, máxima prioridad
        levelUpEvent?.let { level ->
            LevelUpOverlay(
                newLevel = level,
                onDismiss = { profileViewModel.consumeLevelUpEvent() }
            )
        }
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

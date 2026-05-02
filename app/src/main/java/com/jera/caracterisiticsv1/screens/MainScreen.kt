package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*

// ─── Colores usados en MainScreen ──────────────────────────────────────────
// Fondo:              CyberDark    (#110015)
// Botones fondo:      SurfaceColor (#1a0020)
// Botones borde:      AccentPrimary (#ff7037) / CyberYellow (#fff04c)
// Íconos/Texto:       CyberYellow  (#fff04c)
// TopBar fondo:       SurfaceDark  (#0d000f)
// TopBar título:      CyberYellow
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun MainScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxSize(),
        color = CyberDark
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Header(navController)
            Spacer(Modifier.size(50.dp))
            ButtonCamera(navController)
            Spacer(Modifier.size(20.dp))
            ButtonGallery(navController)
            Spacer(Modifier.size(20.dp))
            ButtonGarage(navController)
            Spacer(Modifier.size(20.dp))
            ButtonMap(navController)
        }
    }
}

@Composable
fun ButtonGallery(navController: NavHostController) {
    CyberMenuButton(
        label = "Galería",
        iconRes = R.drawable.gallery,
        onClick = { navController.navigate(AppScreens.GalleryScreen.route) }
    )
}

@Composable
fun ButtonCamera(navController: NavHostController) {
    CyberMenuButton(
        label = "Cámara",
        iconRes = R.drawable.camera,
        onClick = { navController.navigate(AppScreens.CameraScreen.route) }
    )
}

@Composable
fun ButtonGarage(navController: NavHostController) {
    CyberMenuButton(
        label = "Garaje",
        iconRes = R.drawable.car_in_garage,
        onClick = { navController.navigate(AppScreens.GarageScreen.route) }
    )
}

@Composable
fun ButtonMap(navController: NavHostController) {
    CyberMenuButton(
        label = "Mapa",
        iconRes = R.drawable.home,
        onClick = { navController.navigate(AppScreens.MapScreen.route) }
    )
}

/** Botón cuadrado con estilo cyberpunk para el menú principal. */
@Composable
private fun CyberMenuButton(
    label: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(160.dp)
            .width(160.dp)
            .shadow(8.dp, shape = RectangleShape, spotColor = AccentPrimary)
            .border(1.dp, AccentPrimary, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(backgroundColor = SurfaceColor),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = CyberYellow
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = label,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 18.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = CyberYellow,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun Header(navController: NavHostController) {
    TopAppBar(
        backgroundColor = SurfaceDark,
        elevation = 0.dp,
        title = {
            Text(
                text = "COCHEMON",
                modifier = Modifier.padding(4.dp, 4.dp),
                fontSize = 22.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = CyberYellow,
                letterSpacing = 4.sp
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate(AppScreens.SettingsScreen.route) }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Ajustes",
                    modifier = Modifier.size(28.dp),
                    tint = CyberOrange
                )
            }
        }
    )
}

package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*
import kotlinx.coroutines.delay

// ─── Colores usados en SplashScreen ────────────────────────────────────────
// Fondo:       CyberDark   (#110015)
// Título:      CyberYellow (#fff04c)
// Subtítulo:   CyberOrange (#ff7037)
// Rueda/giro:  AccentPrimary vía graphicsLayer
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(1500)
        navController.popBackStack()
        navController.navigate(AppScreens.MapScreen.route)
    }
    Splash()
}

@Composable
fun Splash() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 720F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "wheel_angle"
    )

    // Pulso de brillo cyberpunk
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .background(CyberDark)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Logo principal
        Image(
            painter = painterResource(id = R.drawable.splashscreen),
            contentDescription = "Logo"
        )
        // Rueda giratoria
        Image(
            painter = painterResource(id = R.drawable.splashscreenrueda),
            contentDescription = "LogoRueda",
            modifier = Modifier.graphicsLayer {
                this.rotationZ = angle
            }
        )

        // Texto inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "COCHEMON",
                color = CyberYellow.copy(alpha = glowAlpha),
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 6.sp
            )
            Text(
                text = "IDENTIFY · COLLECT · EXPLORE",
                color = CyberOrange.copy(alpha = glowAlpha * 0.8f),
                fontFamily = Poppins,
                fontSize = 11.sp,
                letterSpacing = 3.sp
            )
        }
    }
}

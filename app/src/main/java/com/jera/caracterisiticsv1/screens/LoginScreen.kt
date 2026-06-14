package com.jera.caracterisiticsv1.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lanzador del intent de Google Sign-In
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    // Navegar al mapa cuando el login sea exitoso
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(AppScreens.MapScreen.route) {
                popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
            }
        }
    }

    // Animación de pulso para el logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark),
        contentAlignment = Alignment.Center
    ) {
        // Fondo con gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentPrimary.copy(alpha = 0.08f),
                            CyberDark
                        ),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Logo / icono ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceColor)
                    .border(
                        2.dp,
                        AccentPrimary.copy(alpha = glowAlpha),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(80.dp)
                )
            }

            // ── Título ────────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Caracteristics",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = AccentPrimary,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "// GARAGE SOCIAL",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = CyberYellow,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Conecta con tu cuenta Google para guardar\ntu progreso y competir con otros cazadores.",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            // ── Separador ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(1.dp)
                    .background(AccentPrimary.copy(alpha = 0.3f))
            )

            // ── Botón Google Sign-In ──────────────────────────────────────────
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = AccentPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                GoogleSignInButton(
                    onClick = {
                        signInLauncher.launch(viewModel.getSignInIntent())
                    }
                )
            }

            // ── Error ─────────────────────────────────────────────────────────
            uiState.error?.let { error ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22FF4444))
                        .border(1.dp, Color(0xFFFF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = error,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFFFF6666),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Saltar login (jugar sin cuenta) ───────────────────────────────
            TextButton(
                onClick = {
                    navController.navigate(AppScreens.MapScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = "Continuar sin cuenta →",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceColor,
            contentColor = TextColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp, AccentPrimary.copy(alpha = 0.7f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono de Google (texto simulado con colores característicos)
            Text(text = "G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            Text(
                text = "INICIAR CON GOOGLE",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextColor,
                letterSpacing = 1.sp
            )
        }
    }
}

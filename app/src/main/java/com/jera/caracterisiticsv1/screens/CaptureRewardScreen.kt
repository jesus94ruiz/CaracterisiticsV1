package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jera.caracterisiticsv1.navigation.AppScreens
import kotlinx.coroutines.delay

// ── Colores del tema ──────────────────────────────────────────────────────────
private val CyberDark = Color(0xFF0A0E1A)
private val CyberDarkCard = Color(0xFF111827)
private val CyberYellow = Color(0xFFFFD700)
private val CyberGreen = Color(0xFF00FF88)
private val CyberBlue = Color(0xFF00BFFF)
private val CyberPurple = Color(0xFF9B59B6)
private val TextPrimary = Color(0xFFE2E8F0)
private val TextSecondary = Color(0xFF94A3B8)

@Composable
fun CaptureRewardScreen(
    navController: NavController,
    xpGained: Int,
    leveledUp: Boolean,
    newLevel: Int,
    achievementsCount: Int
) {
    // ── Estado de animaciones ─────────────────────────────────────────────────
    var animatedXp by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Animación del contador XP
    val xpAnimated by animateIntAsState(
        targetValue = animatedXp,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutExpo),
        label = "xpCounter"
    )

    // Pulsación del badge XP
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Brillo parpadeante
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Alpha de entrada para el contenido
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(500),
        label = "buttonAlpha"
    )

    // Secuencia de entrada
    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
        delay(400)
        animatedXp = xpGained
        delay(1800)
        showButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CyberDark, Color(0xFF0D1B2A), CyberDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = "¡Coche Guardado!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CyberYellow,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Resultados de captura",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Badge XP ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Anillo exterior brillante
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberYellow.copy(alpha = glowAlpha * 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Círculo principal
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF1A2A0A), CyberDarkCard)
                            )
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$xpAnimated",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberYellow,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "XP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── Tarjetas de resultado ─────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Level Up
                if (leveledUp && newLevel > 0) {
                    RewardCard(
                        emoji = "⬆️",
                        title = "¡Subiste de nivel!",
                        subtitle = "Ahora eres nivel $newLevel",
                        accentColor = CyberBlue
                    )
                }

                // Logros desbloqueados
                if (achievementsCount > 0) {
                    RewardCard(
                        emoji = "🏆",
                        title = if (achievementsCount == 1) "¡Logro desbloqueado!"
                                else "¡$achievementsCount logros desbloqueados!",
                        subtitle = if (achievementsCount == 1) "Has completado un nuevo reto"
                                   else "Has completado $achievementsCount nuevos retos",
                        accentColor = CyberPurple
                    )
                }

                // XP ganada siempre visible como tarjeta informativa
                RewardCard(
                    emoji = "⚡",
                    title = "XP Ganada",
                    subtitle = "+$xpGained puntos de experiencia",
                    accentColor = CyberYellow
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Botón continuar ───────────────────────────────────────────────
            Box(modifier = Modifier.alpha(buttonAlpha)) {
                Button(
                    onClick = {
                        navController.navigate(AppScreens.GarageScreen.route) {
                            // Limpiamos el backstack hasta MainScreen
                            popUpTo(AppScreens.MainScreen.route) { inclusive = false }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberYellow,
                        contentColor = CyberDark
                    )
                ) {
                    Text(
                        text = "¡Al Garaje! 🚗",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardCard(
    emoji: String,
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberDarkCard)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

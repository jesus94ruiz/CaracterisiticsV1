package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// Paleta CyberPunk (mirrors res/values/colors.xml)
private val CyberBlack      = Color(0xFF110015)
private val CyberSurface    = Color(0xFF1A0025)
private val CyberSurfaceVar = Color(0xFF25003A)
private val CyberYellow     = Color(0xFFFFF04C)
private val CyberAmber      = Color(0xFFFFC545)
private val CyberOrange     = Color(0xFFFF9B3E)
private val CyberOrangeDark = Color(0xFFFF7037)
private val CyberMagenta    = Color(0xFFA3306F)
private val CyberPurple     = Color(0xFF75108B)
private val CyberGreen      = Color(0xFFB8D14B)

@Composable
fun CaptureRewardScreen(
    navController: NavController,
    xpGained: Int,
    leveledUp: Boolean,
    newLevel: Int,
    achievementsCount: Int
) {
    var animatedXp  by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    var showButton  by remember { mutableStateOf(false) }

    val xpAnimated by animateIntAsState(
        targetValue = animatedXp,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutExpo),
        label = "xpCounter"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(700),
        label = "contentAlpha"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(500),
        label = "buttonAlpha"
    )

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
                    colors = listOf(
                        CyberBlack,
                        Color(0xFF1A0025),
                        Color(0xFF25003A),
                        Color(0xFF1A0025),
                        CyberBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Halo de fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyberPurple.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Titulo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COCHE CAPTURADO",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyberYellow,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CyberYellow,
                                    CyberOrange,
                                    Color.Transparent
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Resultados de captura",
                    fontSize = 13.sp,
                    color = CyberAmber.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }

            // Badge XP circular
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Halo exterior
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberYellow.copy(alpha = glowAlpha * 0.25f),
                                    CyberOrange.copy(alpha = glowAlpha * 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Anillo neón
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    CyberYellow,
                                    CyberOrange,
                                    CyberMagenta,
                                    CyberYellow
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Interior del círculo
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberSurfaceVar,
                                    CyberSurface
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$xpAnimated",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberYellow,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "XP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen,
                            textAlign = TextAlign.Center,
                            letterSpacing = 3.sp
                        )
                    }
                }
            }

            // Tarjetas de resultado
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (leveledUp && newLevel > 0) {
                    CyberRewardCard(
                        emoji = "⬆️",
                        title = "SUBISTE DE NIVEL",
                        subtitle = "Ahora eres nivel $newLevel",
                        accentColor = CyberAmber,
                        borderColor = CyberAmber
                    )
                }

                if (achievementsCount > 0) {
                    CyberRewardCard(
                        emoji = "🏆",
                        title = if (achievementsCount == 1) "LOGRO DESBLOQUEADO"
                                else "${achievementsCount} LOGROS NUEVOS",
                        subtitle = if (achievementsCount == 1) "Has completado un nuevo reto"
                                   else "Has completado $achievementsCount nuevos retos",
                        accentColor = CyberMagenta,
                        borderColor = CyberMagenta
                    )
                }

                CyberRewardCard(
                    emoji = "⚡",
                    title = "XP GANADA",
                    subtitle = "+$xpGained puntos de experiencia",
                    accentColor = CyberOrangeDark,
                    borderColor = CyberOrangeDark
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón Al Garaje
            Box(modifier = Modifier.alpha(buttonAlpha)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CyberYellow.copy(alpha = 0.25f),
                                    CyberOrange.copy(alpha = 0.25f)
                                )
                            )
                        )
                )
                Button(
                    onClick = {
                        // Limpia CameraScreen, ResultsScreen y CaptureRewardScreen
                        // Al hacer back desde GarageScreen vuelve a MapScreen
                        navController.navigate(AppScreens.GarageScreen.route) {
                            popUpTo(AppScreens.MapScreen.route) { inclusive = false }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = CyberBlack
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(CyberYellow, CyberOrange)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "IR AL GARAJE  →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberBlack,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CyberRewardCard(
    emoji: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.7f),
                        borderColor.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icono con fondo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Indicador lateral
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
        }
    }
}

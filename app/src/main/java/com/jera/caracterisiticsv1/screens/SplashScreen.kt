package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.navigation.AppScreens
import com.jera.caracterisiticsv1.ui.theme.*
import kotlinx.coroutines.delay

// ─── SplashScreen Cyberpunk ──────────────────────────────────────────────────
// Fondo:         CyberBlack  (#110015) + gradiente SurfaceColor
// Letra "C":     CyberYellow (#FFF04C) — capas glitch en CyberOrangeDark / CyberMagenta
// Título:        CyberAmber  (#FFC545) — también con glitch
// Scanlines:     CyberPurple (15% alpha)
// Bordes neón:   CyberGreen  (#B8D14B)
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(2800)
        navController.popBackStack()
        navController.navigate(AppScreens.MapScreen.route)
    }
    Splash()
}

@Composable
fun Splash() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_glitch")

    // ── Fade-in de aparición ─────────────────────────────────────────────────
    val fadeIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeIn.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // ── Flicker general (parpadeo suave) ─────────────────────────────────────
    val flicker by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                1.00f at 0
                1.00f at 1200
                0.55f at 1240
                0.75f at 1270
                0.60f at 1290
                1.00f at 1320
                1.00f at 1700
                0.50f at 1730
                0.80f at 1760
                1.00f at 1790
                1.00f at 2400
                0.60f at 2430
                0.70f at 2460
                1.00f at 2490
                1.00f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "flicker"
    )

    // ── Desplazamiento X glitch del bloque principal ──────────────────────────
    val glitchX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                0f  at 0
                0f  at 900
                -8f at 950
                5f  at 980
                0f  at 1010
                0f  at 1600
                6f  at 1640
                -4f at 1670
                0f  at 1700
                0f  at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_x"
    )

    // ── Alpha de los fantasmas de glitch ─────────────────────────────────────
    val ghostAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                0.1f   at 0
                0.2f   at 900
                0.75f at 950
                0.6f at 980
                0.3f   at 1010
                0.2f   at 1600
                0.8f at 1640
                0.5f at 1670
                0.3f   at 1700
                0.1f   at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ghost_alpha"
    )

    // ── Desplazamiento Y de scanlines (scroll lento hacia abajo) ─────────────
    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanlines"
    )

    // ── Brillo pulsante del borde neón ────────────────────────────────────────
    val neonPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceDark, CyberBlack, SurfaceColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // ── Scanlines (efecto CRT) ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineSpacing = 6f
            val totalLines = (size.height / lineSpacing).toInt() + 2
            val offsetPx = scanlineOffset * lineSpacing

            for (i in 0..totalLines) {
                val y = i * lineSpacing + offsetPx - lineSpacing
                drawLine(
                    color = CyberPurple.copy(alpha = 0.12f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
            }
        }

        // ── Contenido central con fade-in + flicker ───────────────────────────
        Column(
            modifier = Modifier
                .graphicsLayer {
                    alpha = fadeIn.value * flicker
                    translationX = glitchX
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Línea decorativa superior ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyberGreen.copy(alpha = neonPulse),
                                CyberYellow.copy(alpha = neonPulse),
                                CyberGreen.copy(alpha = neonPulse),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Letra "C" gigante con capas de glitch ─────────────────────────
            Box(contentAlignment = Alignment.Center) {

                // Ghost 1 — CyberMagenta desplazado a la derecha
                Text(
                    text = "C",
                    color = CyberMagenta.copy(alpha = ghostAlpha),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = 10f
                        translationY = -4f
                    }
                )

                // Ghost 2 — CyberOrangeDark desplazado a la izquierda
                Text(
                    text = "C",
                    color = CyberOrangeDark.copy(alpha = ghostAlpha * 0.85f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = -10f
                        translationY = 4f
                    }
                )

                // Capa principal — CyberYellow
                Text(
                    text = "C",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp
                )
            }

            Spacer(modifier = Modifier.height((-16).dp))

            // ── Título "CARACTERISTICS" con glitch ────────────────────────────
            Box(contentAlignment = Alignment.Center) {

                // Ghost 1 — CyberMagenta
                Text(
                    text = "CARACTERISTICS",
                    color = CyberMagenta.copy(alpha = ghostAlpha * 0.9f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = 8f
                        translationY = -2f
                    }
                )

                // Ghost 2 — CyberOrangeDark
                Text(
                    text = "CARACTERISTICS",
                    color = CyberOrangeDark.copy(alpha = ghostAlpha * 0.75f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = -8f
                        translationY = 2f
                    }
                )

                // Texto principal — CyberAmber
                Text(
                    text = "CARACTERISTICS",
                    color = CyberAmber,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Subtítulo / tagline ───────────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "IDENTIFY  ·  ANALYZE  ·  EXPLORE",
                    color = CyberOrange.copy(alpha = neonPulse * 0.75f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Línea decorativa inferior ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyberGreen.copy(alpha = neonPulse),
                                CyberYellow.copy(alpha = neonPulse),
                                CyberGreen.copy(alpha = neonPulse),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

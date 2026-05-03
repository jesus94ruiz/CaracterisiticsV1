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
                0.1f  at 0
                0.2f  at 900
                0.75f at 950
                0.6f  at 980
                0.3f  at 1010
                0.2f  at 1600
                0.8f  at 1640
                0.5f  at 1670
                0.3f  at 1700
                0.1f  at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ghost_alpha"
    )

    // ── Escala horizontal (stretch/squeeze de píxeles) ────────────────────────
    val glitchScaleX by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                1.00f at 0
                1.00f at 900
                1.09f at 930      // estira
                0.93f at 960      // comprime
                1.04f at 985
                1.00f at 1010
                1.00f at 1600
                0.91f at 1630     // comprime
                1.07f at 1660     // estira
                1.00f at 1695
                1.00f at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_scaleX"
    )

    // ── Escala vertical (compresión de scanlines) ─────────────────────────────
    val glitchScaleY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                1.00f at 0
                1.00f at 900
                0.96f at 940      // aplasta verticalmente
                1.03f at 970
                0.98f at 995
                1.00f at 1020
                1.00f at 1600
                1.04f at 1635
                0.95f at 1660
                1.00f at 1695
                1.00f at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_scaleY"
    )

    // ── Cizalla (skewX) para los fantasmas — simula block tearing ────────────
    val glitchSkew by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4500
                0.00f  at 0
                0.00f  at 900
                0.12f  at 940     // inclina derecha
                -0.09f at 970     // inclina izquierda
                0.05f  at 990
                0.00f  at 1015
                0.00f  at 1600
                -0.11f at 1635
                0.08f  at 1660
                0.00f  at 1695
                0.00f  at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_skew"
    )

    // ── Desplazamiento Y independiente del ghost1 (franja superior) ───────────
    val ghostY1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4500
                0f   at 0
                0f   at 900
                -12f at 940
                6f   at 970
                -4f  at 995
                0f   at 1020
                0f   at 1600
                8f   at 1635
                -6f  at 1660
                0f   at 1695
                0f   at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ghost_y1"
    )

    // ── Desplazamiento Y independiente del ghost2 (franja inferior) ──────────
    val ghostY2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                0f  at 0
                0f  at 900
                10f at 950
                -7f at 975
                3f  at 1000
                0f  at 1025
                0f  at 1600
                -9f at 1640
                5f  at 1665
                0f  at 1700
                0f  at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "ghost_y2"
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

        // ── Contenido central con fade-in ─────────────────────────────────────
        Column(
            modifier = Modifier
                .graphicsLayer {
                    alpha = fadeIn.value
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

                // Ghost 1 — CyberMagenta: desplazado + skew + scaleX propio
                Text(
                    text = "C",
                    color = CyberMagenta.copy(alpha = ghostAlpha),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = 10f
                        translationY = ghostY1
                        scaleX = glitchScaleX * 1.06f
                        scaleY = glitchScaleY
                        rotationZ = glitchSkew * 2f
                    }
                )

                // Ghost 2 — CyberOrangeDark: desplazado + skew inverso
                Text(
                    text = "C",
                    color = CyberOrangeDark.copy(alpha = ghostAlpha * 0.85f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = -10f
                        translationY = ghostY2
                        scaleX = glitchScaleX * 0.94f
                        scaleY = glitchScaleY
                        rotationZ = -glitchSkew * 1.5f
                    }
                )

                // Capa principal — CyberYellow: deformación de píxeles
                Text(
                    text = "C",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 190.sp,
                    letterSpacing = (-4).sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = glitchScaleX
                        scaleY = glitchScaleY
                    }
                )
            }

            Spacer(modifier = Modifier.height((-16).dp))

            // ── Título "CARACTERISTICS" con glitch ────────────────────────────
            Box(contentAlignment = Alignment.Center) {

                // Ghost 1 — CyberMagenta: skew + desplazamiento Y
                Text(
                    text = "CARACTERISTICS",
                    color = CyberMagenta.copy(alpha = ghostAlpha * 0.9f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = 8f
                        translationY = ghostY1 * 0.35f
                        scaleX = glitchScaleX * 1.04f
                        rotationZ = glitchSkew * 1.5f
                    }
                )

                // Ghost 2 — CyberOrangeDark: skew inverso
                Text(
                    text = "CARACTERISTICS",
                    color = CyberOrangeDark.copy(alpha = ghostAlpha * 0.75f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp,
                    modifier = Modifier.graphicsLayer {
                        translationX = -8f
                        translationY = ghostY2 * 0.35f
                        scaleX = glitchScaleX * 0.96f
                        rotationZ = -glitchSkew * 1.2f
                    }
                )

                // Texto principal — CyberAmber: stretch/squeeze
                Text(
                    text = "CARACTERISTICS",
                    color = CyberAmber,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 6.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = glitchScaleX
                        scaleY = glitchScaleY
                    }
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

package com.jera.caracterisiticsv1.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.ui.theme.*
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// XP TOAST — aparece en la parte superior y se desvanece automáticamente
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun XpGainedToast(
    xp: Int,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(xp) {
        visible = true
        delay(1800)
        visible = false
        delay(300)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(CyberOrangeDark.copy(alpha = 0.92f), CyberYellow.copy(alpha = 0.92f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, CyberYellow, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$xp XP ⚡",
                    color = CyberBlack,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LEVEL UP OVERLAY — pantalla completa con animación de pulso
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LevelUpOverlay(
    newLevel: Int,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "levelup_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Destello superior
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                CyberYellow.copy(alpha = glowAlpha),
                                CyberOrangeDark.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(80.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⬆",
                    fontSize = 64.sp,
                    color = CyberYellow
                )
            }

            Text(
                text = "¡NIVEL SUPERADO!",
                color = CyberYellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(CyberOrangeDark, CyberYellow, CyberOrangeDark)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 40.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "NIVEL $newLevel",
                    color = CyberBlack,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Toca para continuar",
                color = CyberWhite.copy(alpha = 0.6f),
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ACHIEVEMENT TOAST — aparece en la parte inferior
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AchievementToast(
    icon: String,
    title: String,
    description: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(title) {
        visible = true
        delay(3000)
        visible = false
        delay(350)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(animationSpec = tween(250)),
        exit = slideOutVertically { it } + fadeOut(animationSpec = tween(350))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(SurfaceColor, CyberGreen.copy(alpha = 0.15f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(CyberGreen, CyberYellow)),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = icon, fontSize = 32.sp)
                Column {
                    Text(
                        text = "🏅 ¡Logro Desbloqueado!",
                        color = CyberGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = title,
                        color = CyberYellow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = description,
                        color = CyberWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

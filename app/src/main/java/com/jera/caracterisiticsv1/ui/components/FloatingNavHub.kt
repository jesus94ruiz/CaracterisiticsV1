package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.ui.theme.*

// ─── FloatingNavHub ───────────────────────────────────────────────────────────
// Botón principal (cámara) rodeado de 3 satélites:
//   • Galería  → arriba-derecha
//   • Garaje   → centro-derecha
//   • Ajustes  → abajo-izquierda
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FloatingNavHub(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onGarageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(170.dp)
    ) {
        // ── Galería (arriba-derecha) ──────────────────────────────────────
        SatelliteButton(
            iconRes = R.drawable.gallery,
            label = "Galería",
            onClick = onGalleryClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // ── Garaje (centro-derecha) ───────────────────────────────────────
        SatelliteButton(
            iconRes = R.drawable.car_in_garage,
            label = "Garaje",
            onClick = onGarageClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        // ── Ajustes (abajo-izquierda) ─────────────────────────────────────
        SatelliteButton(
            iconRes = R.drawable.settings,
            label = "Ajustes",
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // ── Botón principal: Cámara (abajo-derecha) ───────────────────────
        MainCameraButton(
            onClick = onCameraClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

// ─── Botón principal de cámara ────────────────────────────────────────────────
@Composable
private fun MainCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(72.dp)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(CyberOrange, CyberOrangeDark, CyberPurple)
                )
            )
            .border(2.dp, CyberYellow, CircleShape)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Cámara",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(CyberWhite)
        )
    }
}

// ─── Botón satélite pequeño ───────────────────────────────────────────────────
@Composable
private fun SatelliteButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(SurfaceColor)
                .border(1.5.dp, AccentPrimary.copy(alpha = 0.8f), CircleShape)
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(CyberYellow)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            color = CyberYellow.copy(alpha = 0.85f),
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp,
            letterSpacing = 0.8.sp
        )
    }
}

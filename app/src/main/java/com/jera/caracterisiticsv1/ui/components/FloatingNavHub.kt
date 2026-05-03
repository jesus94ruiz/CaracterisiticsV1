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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.R
import com.jera.caracterisiticsv1.ui.theme.*

// ─── FloatingNavHub ───────────────────────────────────────────────────────────
// Botón principal (cámara) en la esquina inferior-derecha del hub.
// Satélites posicionados alrededor del principal:
//   • Garaje   → directamente encima
//   • Galería  → diagonal arriba-izquierda
//   • Ajustes  → a la izquierda
//
// Hub box: 200×200 dp
// Main button center: (164, 164) — offset absoluteOffset(128, 128) para un botón de 72dp
// Órbita: ~80 dp de distancia entre centros
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
        modifier = modifier.size(200.dp)
    ) {
        // ── Garaje: directamente encima del principal ─────────────────────
        // Centro del satélite en (164, 84) → Column offset (142, 62)
        SatelliteButton(
            iconRes = R.drawable.car_in_garage,
            label = "Garaje",
            onClick = onGarageClick,
            modifier = Modifier.absoluteOffset(x = 142.dp, y = 62.dp)
        )

        // ── Galería: diagonal arriba-izquierda (ligeramente más arriba y a la derecha)
        // Centro del satélite en (118, 94) → Column offset (96, 72)
        SatelliteButton(
            iconRes = R.drawable.gallery,
            label = "Galería",
            onClick = onGalleryClick,
            modifier = Modifier.absoluteOffset(x = 88.dp, y = 80.dp)
        )

        // ── Ajustes: a la izquierda del principal ─────────────────────────
        // Centro del satélite en (84, 164) → Column offset (62, 142)
        SatelliteButton(
            iconRes = R.drawable.settings,
            label = "Ajustes",
            onClick = onSettingsClick,
            modifier = Modifier.absoluteOffset(x = 62.dp, y = 142.dp)
        )

        // ── Botón principal: Cámara (abajo-derecha) ───────────────────────
        // Offset (128, 128) → botón de 72dp ocupa (128..200, 128..200)
        MainCameraButton(
            onClick = onCameraClick,
            modifier = Modifier.absoluteOffset(x = 128.dp, y = 128.dp)
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

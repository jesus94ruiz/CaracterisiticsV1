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
//
// Botón principal (cámara) en la esquina inferior-derecha del hub.
// 4 satélites distribuidos uniformemente en el cuadrante superior-izquierdo,
// dividiendo el arco 90-180 grados en 3 intervalos de 30 grados.
//
// Calculo (rev. 4 — radio restaurado a 105dp):
//   Box: 220x220 dp
//   Camara: 82 dp en offset(138,138) -> centro = (179, 179)
//   Radio orbita: 105 dp  |  Satelite: 38 dp (radio=19)
//   Separacion centros adyacentes: 2*105*sin(15) = 54 dp > 38 dp -> sin superposicion
//   offset = (cx + r*cos(th) - 19,  cy - r*sin(th) - 19)
//
//   90  (arriba):       offset = (160, 55)  -> Garaje
//   120 (diag.vert.):   offset = (108, 69)  -> Ranking
//   150 (diag.horiz.):  offset = ( 69, 108) -> Galeria
//   180 (izquierda):    offset = ( 55, 160) -> Ajustes
//
// ─────────────────────────────────────────────────────────────────────────────

private val hubGradient = Brush.linearGradient(
    colors = listOf(
        CyberOrange.copy(alpha = 0.82f),
        CyberOrangeDark.copy(alpha = 0.82f),
        CyberPurple.copy(alpha = 0.82f)
    )
)

@Composable
fun FloatingNavHub(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onGarageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(220.dp)
    ) {
        // 90 deg -> Garaje: directamente encima
        SatelliteButton(
            iconRes = R.drawable.car_in_garage,
            label = "Garaje",
            onClick = onGarageClick,
            modifier = Modifier.absoluteOffset(x = 160.dp, y = 55.dp)
        )

        // 120 deg -> Ranking: diagonal superior vertical
        SatelliteButton(
            iconRes = R.drawable.casco,
            label = "Ranking",
            onClick = onLeaderboardClick,
            modifier = Modifier.absoluteOffset(x = 108.dp, y = 69.dp)
        )

        // 150 deg -> Galeria: diagonal superior horizontal
        SatelliteButton(
            iconRes = R.drawable.gallery,
            label = "Galeria",
            onClick = onGalleryClick,
            modifier = Modifier.absoluteOffset(x = 69.dp, y = 108.dp)
        )

        // 180 deg -> Ajustes: directamente a la izquierda
        SatelliteButton(
            iconRes = R.drawable.settings,
            label = "Ajustes",
            onClick = onSettingsClick,
            modifier = Modifier.absoluteOffset(x = 55.dp, y = 160.dp)
        )

        // Boton principal Camara — esquina inferior-derecha (138+82=220)
        MainCameraButton(
            onClick = onCameraClick,
            modifier = Modifier.absoluteOffset(x = 138.dp, y = 138.dp)
        )
    }
}

@Composable
private fun MainCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(82.dp)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyberOrange.copy(alpha = 0.90f),
                        CyberOrangeDark.copy(alpha = 0.90f),
                        CyberPurple.copy(alpha = 0.90f)
                    )
                )
            )
            .border(2.dp, CyberWhite.copy(alpha = 0.5f), CircleShape)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Camara",
            modifier = Modifier.size(36.dp),
            colorFilter = ColorFilter.tint(CyberWhite)
        )
    }
}

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
                .size(38.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(hubGradient)
                .border(1.5.dp, CyberWhite.copy(alpha = 0.35f), CircleShape)
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(17.dp),
                colorFilter = ColorFilter.tint(CyberWhite)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            color = CyberWhite.copy(alpha = 0.9f),
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp,
            letterSpacing = 0.8.sp
        )
    }
}

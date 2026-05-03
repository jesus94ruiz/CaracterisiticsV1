package com.jera.caracterisiticsv1.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.ui.theme.*

// ─── Modelo de datos: Misión ──────────────────────────────────────────────────
data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,       // 0–100
    val isCompleted: Boolean = false
)

// ─── Placeholders ─────────────────────────────────────────────────────────────
val placeholderMissions = listOf(
    Mission(
        id = "m1",
        title = "Primer escáner",
        description = "Escanea tu primer vehículo con la cámara.",
        progress = 100,
        isCompleted = true
    ),
    Mission(
        id = "m2",
        title = "Coleccionista novato",
        description = "Añade 5 vehículos distintos a tu garaje.",
        progress = 60
    ),
    Mission(
        id = "m3",
        title = "Explorador urbano",
        description = "Detecta vehículos en 3 ciudades diferentes.",
        progress = 33
    ),
    Mission(
        id = "m4",
        title = "Fotógrafo de lujo",
        description = "Captura 10 vehículos de gama alta.",
        progress = 10
    )
)

// ─── MissionsPanel ────────────────────────────────────────────────────────────
// • Colapsado: tarjeta compacta en la esquina inferior izquierda.
// • Expandido: overlay grande semitransparente centrado en pantalla.
//   El estado expandido se gestiona con hoisting en MapScreen.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MissionsCompactCard(
    missions: List<Mission> = placeholderMissions,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = missions.count { it.isCompleted }
    val total = missions.size

    Box(
        modifier = modifier
            .width(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark.copy(alpha = 0.88f))
            .border(
                1.dp,
                AccentPrimary.copy(alpha = 0.55f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onExpand() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "MISIONES",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "$completed/$total",
                    color = AccentPrimary,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            // Barra global
            LinearProgressIndicator(
                progress = if (total > 0) completed.toFloat() / total else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberYellow,
                trackColor = SurfaceVariant
            )
            // Próxima misión activa
            missions.firstOrNull { !it.isCompleted }?.let { next ->
                Text(
                    text = next.title,
                    color = CyberWhite.copy(alpha = 0.75f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Hint de expansión
            Text(
                text = "▲ VER TODO",
                color = AccentPrimary.copy(alpha = 0.7f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 7.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─── Overlay expandido ────────────────────────────────────────────────────────
@Composable
fun MissionsExpandedOverlay(
    missions: List<Mission> = placeholderMissions,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(CyberDark.copy(alpha = 0.75f))
            .clickable { onDismiss() }   // tap fuera para cerrar
    ) {
        // Contenedor central — evita que el click se propague
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.72f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SurfaceColor.copy(alpha = 0.97f),
                            CyberDark.copy(alpha = 0.97f)
                        )
                    )
                )
                .border(
                    1.5.dp,
                    AccentPrimary.copy(alpha = 0.7f),
                    RoundedCornerShape(16.dp)
                )
                .clickable(enabled = false) {}   // consume clicks internos
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "// MISIONES & OBJETIVOS",
                        color = CyberYellow,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "✕",
                        color = AccentPrimary,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Separador
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AccentPrimary.copy(alpha = 0f),
                                    AccentPrimary.copy(alpha = 0.8f),
                                    AccentPrimary.copy(alpha = 0f)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista de misiones
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(missions, key = { it.id }) { mission ->
                        MissionItem(mission = mission)
                    }
                }
            }
        }
    }
}

// ─── Ítem de misión individual ────────────────────────────────────────────────
@Composable
private fun MissionItem(mission: Mission) {
    val progressFraction = mission.progress / 100f
    val statusColor = when {
        mission.isCompleted -> NeonGreen
        mission.progress > 0 -> CyberAmber
        else -> SurfaceLight
    }
    val statusLabel = when {
        mission.isCompleted -> "COMPLETADA"
        mission.progress > 0 -> "EN CURSO  ${mission.progress}%"
        else -> "PENDIENTE"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Título y estado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = mission.title,
                color = if (mission.isCompleted) NeonGreen else CyberWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = statusLabel,
                color = statusColor,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 0.5.sp
            )
        }
        // Descripción
        Text(
            text = mission.description,
            color = CyberWhite.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
        // Barra de progreso
        LinearProgressIndicator(
            progress = progressFraction,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = statusColor,
            trackColor = SurfaceColor
        )
    }
}

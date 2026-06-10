package com.jera.caracterisiticsv1.ui.components

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
import com.jera.caracterisiticsv1.data.database.entities.DailyMissionEntity
import com.jera.caracterisiticsv1.ui.theme.*

private val hudGradient = Brush.linearGradient(
    colors = listOf(
        CyberOrange.copy(alpha = 0.82f),
        CyberOrangeDark.copy(alpha = 0.82f),
        CyberPurple.copy(alpha = 0.82f)
    )
)

// ─── MissionsCompactCard ──────────────────────────────────────────────────────
// Tarjeta compacta que aparece en la esquina del mapa
@Composable
fun MissionsCompactCard(
    missions: List<DailyMissionEntity>,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = missions.count { it.isCompleted }
    val total = missions.size

    Box(
        modifier = modifier
            .width(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(hudGradient)
            .border(1.dp, CyberWhite.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .clickable { onExpand() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Título y contador
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "MISIONES",
                    color = CyberWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "$completed/$total",
                    color = CyberWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            // Barra de progreso global
            LinearProgressIndicator(
                progress = if (total > 0) completed.toFloat() / total else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberWhite,
                trackColor = CyberWhite.copy(alpha = 0.25f)
            )
            // Próxima misión activa
            missions.firstOrNull { !it.isCompleted }?.let { next ->
                Text(
                    text = next.title,
                    color = CyberWhite.copy(alpha = 0.85f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } ?: run {
                if (total > 0) {
                    Text(
                        text = "¡Todo completado!",
                        color = CyberWhite,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
            // Hint de expansión
            Text(
                text = "▲ VER TODO",
                color = CyberWhite.copy(alpha = 0.85f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 7.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─── MissionsExpandedOverlay ──────────────────────────────────────────────────
@Composable
fun MissionsExpandedOverlay(
    missions: List<DailyMissionEntity>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalXp = missions.sumOf { it.xpReward }
    val earnedXp = missions.filter { it.isCompleted }.sumOf { it.xpReward }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(CyberDark.copy(alpha = 0.75f))
            .clickable { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.75f)
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
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "// MISIONES DIARIAS",
                            color = CyberYellow,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "XP disponible: $earnedXp / $totalXp",
                            color = AccentPrimary,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp
                        )
                    }
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

                // Separador decorativo
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

                Spacer(modifier = Modifier.height(4.dp))

                // Subtítulo: se renuevan mañana
                Text(
                    text = "Las misiones se renuevan cada día",
                    color = CyberWhite.copy(alpha = 0.4f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 8.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (missions.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Cargando misiones...",
                            color = CyberWhite.copy(alpha = 0.5f),
                            fontFamily = Poppins,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(missions, key = { it.missionId }) { mission ->
                            MissionItem(mission = mission)
                        }
                    }
                }
            }
        }
    }
}

// ─── MissionItem ──────────────────────────────────────────────────────────────
@Composable
private fun MissionItem(mission: DailyMissionEntity) {
    val progressFraction =
        if (mission.goal > 0) mission.currentProgress.toFloat() / mission.goal else 0f
    val progressPercent = (progressFraction * 100).toInt()

    val statusColor = when {
        mission.isCompleted -> NeonGreen
        mission.currentProgress > 0 -> CyberAmber
        else -> SurfaceLight
    }
    val statusLabel = when {
        mission.isCompleted -> "✓ COMPLETADA"
        mission.currentProgress > 0 -> "${mission.currentProgress}/${mission.goal}"
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
        // Fila superior: título + estado
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

        // Fila inferior: barra de progreso + recompensa XP
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressIndicator(
                progress = progressFraction,
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = SurfaceColor
            )
            Text(
                text = "+${mission.xpReward} XP",
                color = if (mission.isCompleted) NeonGreen else CyberYellow.copy(alpha = 0.7f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp
            )
        }
    }
}

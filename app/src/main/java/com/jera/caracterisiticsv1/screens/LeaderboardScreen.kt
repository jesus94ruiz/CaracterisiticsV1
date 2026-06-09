package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jera.caracterisiticsv1.repository.FirestoreLeaderboardEntry
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.LeaderboardTab
import com.jera.caracterisiticsv1.viewmodels.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AccentPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "// RANKING GLOBAL",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AccentPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Top 20 cazadores de coches",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = CyberYellow)
            }
        }

        // Línea divisoria
        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(alpha = 0.5f)))

        // ── Tabs ──────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LeaderboardTabChip(
                label = "⚡ POR XP",
                selected = uiState.selectedTab == LeaderboardTab.XP,
                onClick = { viewModel.selectTab(LeaderboardTab.XP) },
                modifier = Modifier.weight(1f)
            )
            LeaderboardTabChip(
                label = "🚗 COLECCIÓN",
                selected = uiState.selectedTab == LeaderboardTab.COLLECTION,
                onClick = { viewModel.selectTab(LeaderboardTab.COLLECTION) },
                modifier = Modifier.weight(1f)
            )
        }

        // Línea divisoria
        Box(Modifier.fillMaxWidth().height(1.dp).background(AccentPrimary.copy(alpha = 0.3f)))

        // ── Contenido ─────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = AccentPrimary, strokeWidth = 2.dp, modifier = Modifier.size(40.dp))
                        Text("CARGANDO RANKING...", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CyberYellow, letterSpacing = 2.sp)
                    }
                }
            }

            uiState.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("⚠", fontSize = 36.sp)
                        Text(uiState.error!!, fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary.copy(0.2f), contentColor = AccentPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("REINTENTAR", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            uiState.entries.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🏆", fontSize = 48.sp)
                        Text("Aún no hay jugadores en el ranking.", fontFamily = Poppins, fontSize = 13.sp, color = TextSecondary)
                        Text("¡Sé el primero en capturar coches!", fontFamily = Poppins, fontSize = 12.sp, color = AccentPrimary)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.entries) { index, entry ->
                        LeaderboardRow(
                            position = index + 1,
                            entry = entry,
                            isCurrentUser = entry.uid == uiState.currentUid,
                            tab = uiState.selectedTab
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    position: Int,
    entry: FirestoreLeaderboardEntry,
    isCurrentUser: Boolean,
    tab: LeaderboardTab
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = when {
        isCurrentUser -> CyberYellow
        position == 1 -> Color(0xFFFFD700)   // Oro
        position == 2 -> Color(0xFFC0C0C0)   // Plata
        position == 3 -> Color(0xFFCD7F32)   // Bronce
        else -> SurfaceLight.copy(alpha = 0.2f)
    }
    val bgColor = when {
        isCurrentUser -> CyberYellow.copy(alpha = 0.08f)
        position <= 3 -> borderColor.copy(alpha = 0.06f)
        else -> SurfaceColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Posición
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (position) {
                1 -> Text("🥇", fontSize = 22.sp)
                2 -> Text("🥈", fontSize = 22.sp)
                3 -> Text("🥉", fontSize = 22.sp)
                else -> Text(
                    text = "#$position",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceVariant)
                .border(1.dp, borderColor.copy(alpha = 0.6f), CircleShape)
        ) {
            if (entry.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = entry.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = entry.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AccentPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Info del usuario
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = entry.username,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isCurrentUser) CyberYellow else TextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberYellow.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("TÚ", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = CyberYellow)
                    }
                }
            }
            Text(
                text = "Nv. ${entry.level}",
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = AccentPrimary
            )
        }

        // Métrica principal
        Column(horizontalAlignment = Alignment.End) {
            when (tab) {
                LeaderboardTab.XP -> {
                    Text(
                        text = "${entry.totalXp} XP",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CyberYellow
                    )
                    Text(
                        text = "${entry.carsCollected} coches",
                        fontFamily = Poppins,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
                LeaderboardTab.COLLECTION -> {
                    Text(
                        text = "${entry.carsCollected} 🚗",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AccentPrimary
                    )
                    Text(
                        text = "${entry.totalXp} XP",
                        fontFamily = Poppins,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) AccentPrimary.copy(0.2f) else SurfaceVariant)
            .border(1.5.dp, if (selected) AccentPrimary else SurfaceLight.copy(0.3f), shape)
            .clickable { onClick() }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (selected) AccentPrimary else SurfaceLight,
            letterSpacing = 0.5.sp
        )
    }
}

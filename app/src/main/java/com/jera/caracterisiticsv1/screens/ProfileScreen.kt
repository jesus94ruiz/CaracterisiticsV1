package com.jera.caracterisiticsv1.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import com.jera.caracterisiticsv1.data.database.entities.UserProfileEntity
import com.jera.caracterisiticsv1.ui.theme.*
import com.jera.caracterisiticsv1.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = AccentPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // ── TopBar ────────────────────────────────────────────────────
                item {
                    ProfileTopBar(onBack = { navController.popBackStack() })
                }

                // ── Hero: Avatar + nombre + nivel ─────────────────────────────
                item {
                    ProfileHeroSection(
                        profile = uiState.profile,
                        xpForNextLevel = uiState.xpForNextLevel
                    )
                }

                // ── Estadísticas ──────────────────────────────────────────────
                item {
                    ProfileStatsSection(profile = uiState.profile)
                }

                // ── Logros ────────────────────────────────────────────────────
                item {
                    SectionTitle(text = "LOGROS")
                }

                if (uiState.achievements.isEmpty()) {
                    item { EmptyAchievementsPlaceholder() }
                } else {
                    items(uiState.achievements) { achievement ->
                        AchievementRow(achievement = achievement)
                    }
                }
            }
        }
    }
}

// ─── TopBar ───────────────────────────────────────────────────────────────────
@Composable
private fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberDark)
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = CyberYellow
            )
        }
        Text(
            text = "// PERFIL",
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileHeroSection(
    profile: UserProfileEntity,
    xpForNextLevel: Int
) {
    val xpFraction = if (xpForNextLevel > 0)
        profile.currentXp.toFloat() / xpForNextLevel
    else 1f

    var animationStarted by remember { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (animationStarted) xpFraction.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "xpAnimation"
    )
    LaunchedEffect(Unit) { animationStarted = true }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Avatar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CyberOrange, CyberPurple)
                    )
                )
                .border(2.dp, CyberYellow, CircleShape)
        ) {
            Text(
                text = profile.username.take(2).uppercase(),
                color = CyberWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // Nombre
        Text(
            text = profile.username,
            color = CyberWhite,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(4.dp))

        // Nivel con badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AccentPrimary.copy(alpha = 0.18f))
                .border(1.dp, AccentPrimary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = "NIVEL ${profile.level}",
                color = CyberYellow,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // Barra de XP
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "XP",
                    color = NeonAmber.copy(alpha = 0.8f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${profile.currentXp} / $xpForNextLevel",
                    color = NeonAmber.copy(alpha = 0.8f),
                    fontFamily = Poppins,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = animatedFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = CyberAmber,
                trackColor = SurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        // XP total
        Text(
            text = "XP TOTAL: ${profile.totalXp}",
            color = CyberWhite.copy(alpha = 0.45f),
            fontFamily = Poppins,
            fontSize = 9.sp,
            letterSpacing = 1.sp
        )
    }
}

// ─── Stats ────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileStatsSection(profile: UserProfileEntity) {
    SectionTitle(text = "ESTADÍSTICAS")

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        StatCard(
            label = "COCHES\nÚNICOS",
            value = profile.carsCollected.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "CAPTURAS\nTOTALES",
            value = profile.totalCaptures.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "XP\nTOTAL",
            value = profile.totalXp.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberPurple.copy(alpha = 0.25f),
                        CyberDark
                    )
                )
            )
            .border(1.dp, AccentPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Text(
            text = value,
            color = CyberYellow,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            text = label,
            color = CyberWhite.copy(alpha = 0.65f),
            fontFamily = Poppins,
            fontSize = 8.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Logros ───────────────────────────────────────────────────────────────────
@Composable
private fun AchievementRow(achievement: AchievementEntity) {
    val unlocked = achievement.isUnlocked
    val progressFraction = if (achievement.target > 0)
        achievement.progress.toFloat() / achievement.target
    else if (unlocked) 1f else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (unlocked) AccentPrimary.copy(alpha = 0.12f)
                else CyberDark.copy(alpha = 0.6f)
            )
            .border(
                width = 1.dp,
                color = if (unlocked) AccentPrimary.copy(alpha = 0.7f)
                        else AccentPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        // Icono / emoji
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked)
                        Brush.radialGradient(listOf(CyberOrange, CyberPurple))
                    else
                        Brush.radialGradient(listOf(Color.DarkGray, Color.Black))
                )
        ) {
            Text(
                text = achievement.icon,
                fontSize = 22.sp,
                color = if (unlocked) CyberWhite else CyberWhite.copy(alpha = 0.3f)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = achievement.title,
                    color = if (unlocked) CyberYellow else CyberWhite.copy(alpha = 0.5f),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                if (unlocked) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = CyberYellow,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "${achievement.progress}/${achievement.target}",
                        color = CyberWhite.copy(alpha = 0.4f),
                        fontFamily = Poppins,
                        fontSize = 9.sp
                    )
                }
            }
            Text(
                text = achievement.description,
                color = CyberWhite.copy(alpha = if (unlocked) 0.75f else 0.4f),
                fontFamily = Poppins,
                fontSize = 9.sp
            )
            if (!unlocked) {
                Spacer(Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = progressFraction.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyberAmber,
                    trackColor = SurfaceVariant
                )
            }
            Text(
                text = "+${achievement.xpReward} XP",
                color = NeonAmber.copy(alpha = if (unlocked) 0.9f else 0.4f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AccentPrimary,
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 3.sp,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 6.dp)
    )
}

@Composable
private fun EmptyAchievementsPlaceholder() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, AccentPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "Sin logros todavía.\n¡Empieza a capturar coches!",
            color = CyberWhite.copy(alpha = 0.4f),
            fontFamily = Poppins,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

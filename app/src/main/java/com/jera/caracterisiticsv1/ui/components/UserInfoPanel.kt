package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jera.caracterisiticsv1.ui.theme.*

// ─── Modelo de datos: Usuario ─────────────────────────────────────────────────
data class UserInfo(
    val name: String,
    val level: Int,
    val currentXp: Int,
    val maxXp: Int,
    val avatarInitials: String = name.take(2).uppercase()
)

val placeholderUser = UserInfo(
    name = "JesusDrv",
    level = 12,
    currentXp = 3400,
    maxXp = 5000
)

private val hudGradient = Brush.linearGradient(
    colors = listOf(
        CyberOrange.copy(alpha = 0.82f),
        CyberOrangeDark.copy(alpha = 0.82f),
        CyberPurple.copy(alpha = 0.82f)
    )
)

// ─── UserInfoPanel ────────────────────────────────────────────────────────────
@Composable
fun UserInfoPanel(
    user: UserInfo = placeholderUser,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val xpFraction = if (user.maxXp > 0) user.currentXp.toFloat() / user.maxXp else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(hudGradient)
            .border(1.dp, CyberWhite.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        // ── Avatar circular ───────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(CyberWhite.copy(alpha = 0.20f))
                .border(1.5.dp, CyberWhite.copy(alpha = 0.6f), CircleShape)
        ) {
            Text(
                text = user.avatarInitials,
                color = CyberWhite,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        // ── Info textual + XP ─────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.widthIn(min = 90.dp, max = 130.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = user.name,
                    color = CyberWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Text(
                    text = "LVL ${user.level}",
                    color = CyberWhite,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                )
            }

            // Barra XP
            LinearProgressIndicator(
                progress = xpFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CyberWhite,
                trackColor = CyberWhite.copy(alpha = 0.25f)
            )

            // XP numérico
            Text(
                text = "${user.currentXp} / ${user.maxXp} XP",
                color = CyberWhite.copy(alpha = 0.80f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 7.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

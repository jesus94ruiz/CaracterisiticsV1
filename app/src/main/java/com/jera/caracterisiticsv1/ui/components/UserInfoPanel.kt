package com.jera.caracterisiticsv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

// Placeholder por defecto
val placeholderUser = UserInfo(
    name = "JesusDrv",
    level = 12,
    currentXp = 3400,
    maxXp = 5000
)

// ─── UserInfoPanel ────────────────────────────────────────────────────────────
// Rectángulo superior-derecha con:
//   • Avatar circular con iniciales (placeholder, fácil de sustituir por imagen)
//   • Nombre de usuario
//   • Nivel (LVL XX)
//   • Barra de XP
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun UserInfoPanel(
    user: UserInfo = placeholderUser,
    modifier: Modifier = Modifier
) {
    val xpFraction = if (user.maxXp > 0) user.currentXp.toFloat() / user.maxXp else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark.copy(alpha = 0.88f))
            .border(
                1.dp,
                AccentPrimary.copy(alpha = 0.55f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        // ── Avatar circular ───────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyberOrange, CyberPurple)
                    )
                )
                .border(1.5.dp, CyberYellow, CircleShape)
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
            // Nombre + nivel en la misma fila
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
                    color = CyberYellow,
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
                color = CyberAmber,
                trackColor = SurfaceVariant
            )

            // XP numérico
            Text(
                text = "${user.currentXp} / ${user.maxXp} XP",
                color = NeonAmber.copy(alpha = 0.75f),
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 7.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

package com.jera.caracterisiticsv1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jera.caracterisiticsv1.ui.theme.*

// ─── Colores usados en SettingsScreen ───────────────────────────────────────
// Fondo:              CyberDark      (#110015)
// Header bar:         SurfaceColor   (#1a0020)
// Título:             CyberYellow    (#fff04c)
// Ítem fondo:         SurfaceColor   (#1a0020)
// Ítem borde:         AccentPrimary  (#ff7037) 20% alpha
// Ítem texto:         CyberWhite     (#ffffff)
// Ítem hover/index:   NeonPurple     (#a3306f) / AccentOrange (#ff9b3e)
// Separador:          AccentPrimary  (#ff7037) 30% alpha
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsOptions = listOf(
        "Permisos",
        "Ubicación para guardar fotos",
        "Iniciar sesión",
        "Cerrar sesión",
        "Información",
        "Referencia Api IA",
        "Créditos"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CyberDark
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor)
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = "// AJUSTES",
                    color = CyberYellow,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            // Línea separadora neón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AccentPrimary.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(settingsOptions) { index, item ->
                    SettingItem(
                        text = item,
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
fun SettingItem(text: String, index: Int = 0) {
    // Alterna el color del indicador izquierdo entre AccentPrimary y NeonPurple
    val accentColor = if (index % 2 == 0) AccentPrimary else NeonPurple

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceColor)
            .border(
                width = 1.dp,
                color = AccentPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onSettingClicked(text) }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador lateral de color
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = CyberWhite,
            fontFamily = Poppins,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = ">",
            color = AccentOrange.copy(alpha = 0.6f),
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

fun onSettingClicked(settingName: String) {
    println("Clicked on setting: $settingName")
}

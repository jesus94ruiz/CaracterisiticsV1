package com.jera.caracterisiticsv1.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

// ── Paleta de colores del tema Material ──────────────────────────────────────
// primary          → CyberYellow   (#fff04c)  – accent principal
// primaryVariant   → CyberOrangeDark (#ff7037) – variante / botones
// secondary        → CyberGreen    (#b8d14b)  – accent especial
// secondaryVariant → CyberAmber    (#ffc545)  – variante secundaria
// background       → CyberBlack   (#110015)  – fondo general
// surface          → SurfaceColor  (#1a0025)  – fondo de cards
// error            → CyberRed      (#d15053)  – errores
// ─────────────────────────────────────────────────────────────────────────────

private val CyberpunkDarkColorPalette = darkColors(
    primary          = CyberYellow,
    primaryVariant   = CyberOrangeDark,
    secondary        = CyberGreen,
    secondaryVariant = CyberAmber,
    background       = CyberBlack,
    surface          = SurfaceColor,
    error            = CyberRed,
    onPrimary        = CyberBlack,
    onSecondary      = CyberBlack,
    onBackground     = CyberWhite,
    onSurface        = CyberWhite,
    onError          = CyberWhite
)

@Composable
fun CaracterisiticsV1Theme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = CyberpunkDarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

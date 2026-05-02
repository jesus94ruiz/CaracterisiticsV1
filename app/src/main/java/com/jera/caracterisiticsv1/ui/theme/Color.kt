package com.jera.caracterisiticsv1.ui.theme

import androidx.compose.ui.graphics.Color

// ── Paleta Cyberpunk ─────────────────────────────────────────────────────────
val CyberWhite      = Color(0xFFFFFFFF)   // Blanco puro – texto principal
val CyberYellow     = Color(0xFFFFF04C)   // Amarillo neón – accent primario
val CyberAmber      = Color(0xFFFFC545)   // Ámbar – accent secundario / años
val CyberOrange     = Color(0xFFFF9B3E)   // Naranja medio
val CyberOrangeDark = Color(0xFFFF7037)   // Naranja oscuro – botones / iconos
val CyberRed        = Color(0xFFD15053)   // Rojo oscuro – errores / alertas
val CyberMagenta    = Color(0xFFA3306F)   // Magenta – gradiente medio
val CyberPurple     = Color(0xFF75108B)   // Púrpura – gradiente oscuro
val CyberBlack      = Color(0xFF110015)   // Negro-púrpura – background principal
val CyberGreen      = Color(0xFFB8D14B)   // Verde lima – accent especial / mapa

// ── Alias semánticos (usados en el tema) ─────────────────────────────────────
val BackgroundColor  = CyberBlack
val CyberDark        = CyberBlack          // Alias directo – background principal
val SurfaceColor     = Color(0xFF1A0025)   // Superficie de cards – un tono más claro
val SurfaceDark      = Color(0xFF0D000F)   // Superficie oscura – TopBar / headers
val SurfaceVariant   = Color(0xFF25003A)   // Variante superficie
val SurfaceLight     = Color(0xFF4D3352)   // Tono medio – iconos inactivos BottomNav
val TextColor        = CyberWhite
val TextSecondary    = CyberAmber
val AccentPrimary    = CyberOrangeDark     // Naranja neón – botones / loading / bordes
val AccentSecondary  = CyberYellow         // Amarillo neón – item activo BottomNav
val NeonAmber        = CyberAmber          // Alias – textos de estado / años
val NeonOrange       = CyberOrange         // Alias – naranja neón medio / ResultsScreen
val NeonPurple       = CyberMagenta        // Alias – elementos magenta
val AccentOrange     = CyberOrange         // Alias – texto arteriales mapa
val NeonGreen        = CyberGreen
val ErrorColor       = CyberRed

// ── Colores de mapa (estilo mini-mapa cyberpunk) ──────────────────────────────
val MapBackground    = Color(0xFF0A0010)   // Fondo del mapa
val MapRoad          = Color(0xFF2A1040)   // Carreteras oscuras
val MapRoadHighway   = Color(0xFF4A1060)   // Autopistas
val MapWater         = Color(0xFF0D0020)   // Agua
val MapBuilding      = Color(0xFF1E0030)   // Edificios
val MapPark          = Color(0xFF0F1A05)   // Zonas verdes apagadas
val MapMarker        = CyberYellow         // Marcador de posición
val MapBorderNeon    = CyberGreen          // Borde/contorno neón del mapa

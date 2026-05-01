package com.jera.caracterisiticsv1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val TealFireColorPalette = darkColors(
    primary = Amber,
    primaryVariant = BurntOrange,
    secondary = WarmSand,
    secondaryVariant = SageMint,
    background = DeepInk,
    surface = DeepTeal,
    error = BrickRed,
    onPrimary = DeepInk,
    onSecondary = DeepInk,
    onBackground = WarmSand,
    onSurface = WarmSand,
    onError = WarmSand
)

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = BackgroundColor
)

@Composable
fun CaracterisiticsV1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = TealFireColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

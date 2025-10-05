package com.example.robot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,         // Botones y acciones principales
    secondary = CyanAccent,     // Detalles/acento
    background = NightBlue,     // Fondo principal
    surface = SpaceGray,        // Superficies, cards
    onPrimary = TextPrimary,    // Texto sobre primario
    onSecondary = TextPrimary,  // Texto sobre acentos
    onBackground = TextPrimary, // Texto principal sobre fondo
    onSurface = TextPrimary     // Texto sobre superficies
)

@Composable
fun RobotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
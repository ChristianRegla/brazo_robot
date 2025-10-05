package com.example.robot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BlueTech,
    secondary = CyanTech,
    tertiary = GreenSensor,
    background = BlueDark,
    surface = BlueDark,
    onPrimary = White,
    onSecondary = White,
    onBackground = GrayLight,
    onSurface = GrayLight
)

private val LightColorScheme = lightColorScheme(
    primary = BlueTech,
    secondary = CyanTech,
    tertiary = GreenSensor,
    background = GrayLight,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = BlueDark,
    onSurface = BlueDark
)

@Composable
fun RobotTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
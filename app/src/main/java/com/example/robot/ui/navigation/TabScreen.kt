package com.example.robot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabScreen(
    val title: String,
    val icon: ImageVector,
    val label: String
) {
    object Table : TabScreen(
        title = "Tabla del Robot",
        icon = Icons.Default.TableChart,
        label = "Tabla"
    )

    object Chart : TabScreen(
        title = "Gráfica del Robot",
        icon = Icons.Default.AreaChart,
        label = "Gráfica"
    )
}

val tabs = listOf(
    TabScreen.Table,
    TabScreen.Chart
)
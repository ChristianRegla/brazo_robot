package com.example.robot.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.robot.ui.components.RobotChart
import com.example.robot.ui.components.RobotTable
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onExit: () -> Unit
) {

    var selectedScreen by remember { mutableIntStateOf(0) }
    val headers = listOf("Color", "Peso (g)", "¿Es metal?", "Categoría")
    var rows by remember {
        mutableStateOf(
            listOf(
                listOf("Rojo", "50g", "Verdadero", "Botella"),
                listOf("Verde", "100g", "Falso", "Plástico"),
                listOf("Azul", "800g", "Verdadero", "Botella")
            )
        )
    }
    RobotTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (selectedScreen == 0) "Tabla del Robot" else "Gráfica del Robot",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onGoHome) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Inicio",
                                tint = NeonBlue
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onExit) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = "Salir",
                                tint = NeonBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SpaceGray
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = SpaceGray
                ) {
                    NavigationBarItem(
                        selected = selectedScreen == 0,
                        onClick = { selectedScreen = 0 },
                        icon = {
                            val scale by animateFloatAsState(
                                targetValue = if (selectedScreen == 0) 1.2f else 1f,
                                animationSpec = spring(
                                    stiffness = 400f,
                                    dampingRatio = 0.4f
                                ),
                                label = "scale"
                            )
                            Icon(
                                Icons.Filled.TableChart,
                                contentDescription = "Tabla",
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                tint = if (selectedScreen == 0) NeonBlue else TextPrimary,
                            )
                        },
                        label = {
                            val labelColor by animateColorAsState(
                                targetValue = if (selectedScreen == 0) NeonBlue else TextPrimary,
                                label = "labelColor"
                            )
                            Text(text = "Tabla", color = labelColor)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonBlue.copy(alpha = 0.25f),
                            selectedIconColor = NeonBlue,
                            selectedTextColor = NeonBlue,
                            unselectedIconColor = TextPrimary,
                            unselectedTextColor = TextPrimary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedScreen == 1,
                        onClick = { selectedScreen = 1 },
                        icon = {
                            val scale by animateFloatAsState(
                                targetValue = if (selectedScreen == 1) 1.2f else 1f,
                                animationSpec = spring(
                                    stiffness = 400f,
                                    dampingRatio = 0.4f
                                ),
                                label = "scale"
                            )
                            Icon(
                                Icons.Filled.BarChart,
                                contentDescription = "Gráfica",
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                tint = if (selectedScreen == 1) NeonBlue else TextPrimary
                            )
                        },
                        label = {
                            val labelColor by animateColorAsState(
                                targetValue = if (selectedScreen == 1) NeonBlue else TextPrimary,
                                label = "labelColor"
                            )
                            Text(text = "Gráfica", color = labelColor)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonBlue.copy(alpha = 0.25f),
                            selectedIconColor = NeonBlue,
                            selectedTextColor = NeonBlue,
                            unselectedIconColor = TextPrimary,
                            unselectedTextColor = TextPrimary
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                NightBlue,
                                SpaceGray,
                                DeepBlue
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, 1000f)
                        )
                    )

                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    AnimatedContent(
                        targetState = selectedScreen,
                        transitionSpec = {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut())
                        }
                    ) { targetScreen ->
                        when (targetScreen) {
                            0 -> RobotTable(
                                headers = headers,
                                rows = rows,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(bottom = 16.dp)
                            )
                            1 -> RobotChart(
                                    headers = headers,
                                    rows = rows,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(bottom = 16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(onGoHome = {}, onExit = {})
}
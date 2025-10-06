package com.example.robot.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.robot.ui.components.RobotChart
import com.example.robot.ui.components.RobotTable
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.MaterialViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onExit: () -> Unit
) {
    var selectedScreen by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { 2 },
        initialPage = selectedScreen
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        selectedScreen = pagerState.currentPage
    }

    val headers = listOf("Color", "Peso (g)", "¿Es metal?", "Categoría")
    val materialViewModel: MaterialViewModel = viewModel()
    val materiales by materialViewModel.materiales.collectAsState()
    val isLoading by materialViewModel.isLoading.collectAsState()


    LaunchedEffect(Unit) {
        materialViewModel.loadMateriales()
    }

    val rows = materiales.map { item ->
        listOf(
            item.color,
            "${item.pesoGramos}g",
            if (item.esMetal) "Si" else "No",
            item.categoria
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
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
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
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
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
                                Icons.Filled.AreaChart,
                                contentDescription = "Gráfica",
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                tint = if (selectedScreen == 1) NeonBlue else TextPrimary,
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
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonBlue,
                            trackColor = SpaceGray,
                            strokeWidth = 5.dp
                        )
                    }
                } else if (rows.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay datos disponibles.",
                            color = NeonBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp, start = 12.dp, end = 12.dp)
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 32.dp, start = 12.dp, end = 12.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            when (page) {
                                0 -> RobotTable(
                                    headers = headers,
                                    rows = rows,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )

                                1 -> RobotChart(
                                    headers = headers,
                                    rows = rows,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RobotTheme {
        // Puedes mostrar directamente RobotTable con datos mock
        val fakeRows = listOf(
            listOf("Rojo", "100g", "Si", "Botella"),
            listOf("Verde", "250g", "No", "Plástico"),
            listOf("Azul", "800g", "Si", "Botella")
        )
        RobotTable(
            headers = listOf("Color", "Peso (g)", "¿Es metal?", "Categoría"),
            rows = fakeRows,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
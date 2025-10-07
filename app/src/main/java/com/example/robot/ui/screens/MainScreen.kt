package com.example.robot.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.robot.ui.components.RobotChart
import com.example.robot.ui.components.RobotTable
import com.example.robot.ui.navigation.TabScreen
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.R
import com.example.robot.ui.components.AnimatedNavigationBarItem
import com.example.robot.ui.navigation.tabs
import kotlin.math.roundToInt

private const val INITIALPAGE= 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onExit: () -> Unit
) {

    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = INITIALPAGE
    )
    val coroutineScope = rememberCoroutineScope()

    val headers = listOf(
        stringResource(R.string.Color),
        stringResource(R.string.Peso),
        stringResource(R.string.EsMetal),
        stringResource(R.string.Categoria)
    )

    val materialViewModel: MaterialViewModel = viewModel()
    val isLoading by materialViewModel.isLoading.collectAsState()
    val isConnected by materialViewModel.isConnected.collectAsState()
    val rows by materialViewModel.materialesRows.collectAsState()

    RobotTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = tabs[pagerState.currentPage].title,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onGoHome) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = stringResource(R.string.Inicio),
                                tint = NeonBlue
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onExit) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = stringResource(R.string.Salir),
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
                val itemCount = tabs.size
                val indicatorWidth = 80.dp
                val indicatorHeight = 32.dp
                val indicatorVerticalPadding = 12.dp

                val density = LocalDensity.current
                val windowInfo = LocalWindowInfo.current
                val containerWidthPx = windowInfo.containerSize.width.toFloat()

                val spacerPerItemPx = containerWidthPx / itemCount
                val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

                val targetOffset = pageOffset * spacerPerItemPx + (spacerPerItemPx - with(density) { indicatorWidth.toPx() }) / 2

                val animatedIndicatorOffsetPx by animateFloatAsState(
                    targetValue = targetOffset,
                    animationSpec = spring(stiffness = 400f, dampingRatio = 0.7f),
                    label = stringResource(R.string.indicatorOffset)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(SpaceGray)
                ) {
                    NavigationBar(
                        containerColor = SpaceGray
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            AnimatedNavigationBarItem(
                                isSelected = pagerState.currentPage == index,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                                icon = tab.icon,
                                label = tab.label,
                                contentDescription = "Ir a ${tab.title}"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(animatedIndicatorOffsetPx.roundToInt(), with(density) { indicatorVerticalPadding.toPx() }.roundToInt()) }
                            .size(width = indicatorWidth, height = indicatorHeight)
                            .background(
                                color = NeonBlue.copy(alpha = 0.25f),
                                shape = MaterialTheme.shapes.medium
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
                } else if (!isConnected) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { materialViewModel.loadMateriales() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.wifi_off),
                            contentDescription = stringResource(R.string.iconoSinConexion),
                            tint = NeonBlue,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.sinConexion),
                            color = NeonBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.reintentar),
                            color = NeonBlue,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                } else if (rows.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.file_sad),
                            contentDescription = stringResource(R.string.iconoArchivoVacio),
                            tint = NeonBlue,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = stringResource(R.string.noHayDatos),
                            color = NeonBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            when (tabs[pageIndex]) {
                                is TabScreen.Table -> RobotTable(headers = headers, rows = rows)
                                is TabScreen.Chart -> RobotChart(rows = rows)
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
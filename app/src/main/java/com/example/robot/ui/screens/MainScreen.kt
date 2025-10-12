package com.example.robot.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.robot.ui.components.ConfirmationDialog
import com.example.robot.ui.navigation.tabs
import kotlin.math.roundToInt

private const val INITIALPAGE= 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = INITIALPAGE
    )
    val coroutineScope = rememberCoroutineScope()

    val headers = remember {
        listOf(
            "Color",
            "Peso (g)",
            "¿Es Metal?",
            "Categoría"
        )
    }

    val materialViewModel: MaterialViewModel = viewModel()
    val isLoading by materialViewModel.isLoading.collectAsState()
    val isConnected by materialViewModel.isConnected.collectAsState()
    val materiales by materialViewModel.materiales.collectAsState()

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

    var showClearConfirmationDialog by remember { mutableStateOf(false) }

    if (showClearConfirmationDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.confirmarEliminacionTotalTitulo),
            text = stringResource(R.string.confirmacionEliminacionTotal),
            onConfirm = { materialViewModel.clearAllMateriales() },
            onDismiss = { showClearConfirmationDialog = false }
        )
    }

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
                        IconButton(onClick = {
                            showClearConfirmationDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
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
                val configuration = LocalConfiguration.current

                val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                val indicatorWidthPx = with(density) { indicatorWidth.toPx() }
                val indicatorVerticalPaddingPx = with(density) { indicatorVerticalPadding.toPx() }

                val spacePerItemPx = screenWidthPx / itemCount
                val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

                val indicatorCenterOffset = (spacePerItemPx - indicatorWidthPx) / 2
                val targetOffset = pageOffset * spacePerItemPx + indicatorCenterOffset

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
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage == index) {
                                            when (tabs[index]) {
                                                is TabScreen.Table ->
                                                    lazyListState.animateScrollToItem(0)
                                                is TabScreen.Chart ->
                                                    scrollState.animateScrollTo(0)
                                            }
                                        } else {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                                },
                                icon = tab.icon,
                                label = tab.label,
                                contentDescription = "Ir a ${tab.title}"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    animatedIndicatorOffsetPx.roundToInt(),
                                    indicatorVerticalPaddingPx.roundToInt()
                                )
                            }
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
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(NightBlue, SpaceGray, DeepBlue),
                                start = Offset(0f, 0f),
                                end = Offset(0f, 1000f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                color = NeonBlue,
                                trackColor = SpaceGray,
                                strokeWidth = 5.dp
                            )
                        }

                        !isConnected -> {
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
                        }

                        materiales.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
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
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = 12.dp,
                                        start = 12.dp,
                                        end = 12.dp,
                                        bottom = 16.dp
                                    ),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                when (tabs[pageIndex]) {
                                    is TabScreen.Table -> RobotTable(
                                        headers = headers,
                                        materiales = materiales,
                                        lazyListState = lazyListState,
                                        viewModel = materialViewModel
                                    )

                                    is TabScreen.Chart -> RobotChart(
                                        materiales = materiales,
                                        scrollState = scrollState
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
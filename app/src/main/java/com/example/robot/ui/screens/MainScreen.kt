package com.example.robot.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.robot.ui.navigation.TabScreen
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.ui.components.ConfirmationDialog
import com.example.robot.ui.components.CustomUndoBar
import com.example.robot.ui.components.MainScreenBottomAppBar
import com.example.robot.ui.components.MainScreenDialogs
import com.example.robot.ui.components.MainScreenPagerContent
import com.example.robot.ui.components.MainScreenTopAppBar
import com.example.robot.ui.components.MaterialDetailsBottomSheet
import com.example.robot.ui.navigation.tabs
import com.example.robot.viewmodel.SettingsViewModel

private const val INITIALPAGE= 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToSettings: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = INITIALPAGE
    )
    val coroutineScope = rememberCoroutineScope()

    val materialViewModel: MaterialViewModel = viewModel()

    val isLoading by materialViewModel.isLoading.collectAsState()
    val isConnected by materialViewModel.isConnected.collectAsState()
    val materiales by materialViewModel.sortedMateriales.collectAsState()
    val selectedItems by materialViewModel.selectedItems.collectAsState()
    val lastDeletedItems by materialViewModel.lastDeletedItems.collectAsState()
    val sortState by materialViewModel.sortState.collectAsState()
    val weightStatistics by materialViewModel.weightStatistics.collectAsState()
    val weightDistribution by materialViewModel.weightDistribution.collectAsState()
    val itemsPendientes by materialViewModel.itemsPendientes.collectAsStateWithLifecycle()

    val itemAPreconfirmar = itemsPendientes.firstOrNull()

    val selectedUnit by settingsViewModel.unitType.collectAsStateWithLifecycle()
    val hapticEnabled by settingsViewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val confirmDeleteSelected by settingsViewModel.confirmDeleteSelected.collectAsStateWithLifecycle()
    val confirmDeleteAll by settingsViewModel.confirmDeleteAll.collectAsStateWithLifecycle()
    val undoDuration by settingsViewModel.undoDurationMillis.collectAsStateWithLifecycle()

    var itemDetalle by remember { mutableStateOf<MaterialItem?>(null) }

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var showClearConfirmationDialog by remember { mutableStateOf(false) }
    var showDeleteSelectedConfirmationDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    val colorFilter by materialViewModel.colorFilter.collectAsStateWithLifecycle()
    val isMetalFilter by materialViewModel.isMetalFilter.collectAsStateWithLifecycle()
    val categoryFilter by materialViewModel.categoryFilter.collectAsStateWithLifecycle()

    LaunchedEffect(colorFilter, isMetalFilter, categoryFilter) {
        if (materiales.isNotEmpty() && lazyListState.firstVisibleItemIndex > 0) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    val loadingComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val noInternetComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.no_connection)
    )
    val emptyListComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.empty)
    )

    val loadingProgress by animateLottieCompositionAsState(
        composition = loadingComposition,
        iterations = LottieConstants.IterateForever
    )
    val noInternetProgress by animateLottieCompositionAsState(
        composition = noInternetComposition,
        iterations = LottieConstants.IterateForever
    )
    val emptyListProgress by animateLottieCompositionAsState(
        composition = emptyListComposition,
        iterations = LottieConstants.IterateForever
    )

    BackHandler(enabled = selectedItems.isNotEmpty()) {
        materialViewModel.deselectAllItems()
    }

    LaunchedEffect(materiales) {
        if (materiales.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (selectedItems.isNotEmpty()) {
            materialViewModel.deselectAllItems()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "backgroundTransition")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surface
        ),
        start = Offset(0f, 0f),
        end = Offset(x = animatedOffset / 2, y = 1000f + animatedOffset / 3)
    )

    RobotTheme {
        Scaffold(
            topBar = {
                MainScreenTopAppBar(
                    selectedItems = selectedItems,
                    currentPageTitle = tabs[pagerState.currentPage].title,
                    onGoHomeClick = onGoHome,
                    onDeselectAllClick = { materialViewModel.deselectAllItems() },
                    onDeleteSelectedClick = {
                        if (confirmDeleteSelected) {
                            showDeleteSelectedConfirmationDialog = true
                        } else {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            materialViewModel.deleteSelectedItems(undoDuration.toLong())
                        }
                    },
                    showOptionsMenu = showOptionsMenu,
                    onDismissOptionsMenu = { showOptionsMenu = false },
                    onGoToSettings = onGoToSettings,
                    onGoToAbout = onGoToAbout,
                    onClearListClick = {
                        if (confirmDeleteAll) {
                            showClearConfirmationDialog = true
                        } else {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            materialViewModel.deleteAllMateriales(undoDuration.toLong())
                        }
                    },
                    onOptionsIconClick = { showOptionsMenu = true }
                )
            },
            bottomBar = {
                MainScreenBottomAppBar(
                    pagerState = pagerState,
                    tabs = tabs,
                    coroutineScope = coroutineScope,
                    onTabClick = { index, tab ->
                        coroutineScope.launch {
                            if (pagerState.currentPage == index) {
                                when (tab) {
                                    is TabScreen.Table -> lazyListState.animateScrollToItem(0)
                                    is TabScreen.Chart -> scrollState.animateScrollTo(0)
                                }
                            } else {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
        ) { innerPadding ->
            if (itemAPreconfirmar != null) {
                ConfirmationDialog(
                    title = "Acción Requerida",
                    text = "Nuevo material detectado: ${itemAPreconfirmar.categoria}.\n¿Deseas que el robot lo acomode?",
                    onConfirm = {
                        materialViewModel.confirmarMaterial(itemAPreconfirmar)
                    },
                    onDismiss = {}
                )
            }
            MainScreenDialogs(
                showClearConfirmation = showClearConfirmationDialog,
                onClearConfirm = { materialViewModel.deleteAllMateriales(undoDuration.toLong()) },
                onClearDismiss = { showClearConfirmationDialog = false },
                showDeleteSelectedConfirmation = showDeleteSelectedConfirmationDialog,
                onDeleteSelectedConfirm = { materialViewModel.deleteSelectedItems(undoDuration.toLong()) },
                onDeleteSelectedDismiss = { showDeleteSelectedConfirmationDialog = false },
                hapticEnabled = hapticEnabled,
                haptic = haptic
            )
            MaterialDetailsBottomSheet(
                item = itemDetalle,
                sheetState = sheetState,
                onDismiss = { itemDetalle = null },
                onDeleteClick = { itemToDelete ->
                    materialViewModel.deleteSingleMaterial(itemToDelete, undoDuration.toLong())
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()

                ) { pageIndex ->
                    MainScreenPagerContent(
                        isLoading = isLoading,
                        isConnected = isConnected,
                        materiales = materiales,
                        lastDeletedItems = lastDeletedItems,
                        loadingComposition = loadingComposition,
                        loadingProgress = loadingProgress,
                        noInternetComposition = noInternetComposition,
                        noInternetProgress = noInternetProgress,
                        emptyListComposition = emptyListComposition,
                        emptyListProgress = emptyListProgress,
                        pageIndex = pageIndex,
                        tabs = tabs,
                        materialViewModel = materialViewModel,
                        lazyListState = lazyListState,
                        selectedItems = selectedItems,
                        sortState = sortState,
                        onItemClick = { item ->
                            if (selectedItems.isNotEmpty()) {
                                materialViewModel.toggleItemSelection(item)
                            } else {
                                itemDetalle = item
                                scope.launch { sheetState.show() }
                            }
                        },
                        onItemLongClick = { item ->
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            materialViewModel.toggleItemSelection(item)
                        },
                        onSortClick = { column -> materialViewModel.updateSortColumn(column) },
                        currentUnit = selectedUnit,
                        scrollState = scrollState,
                        onRetryConnection = { materialViewModel.fetchMateriales() },
                        weightStatistics = weightStatistics,
                        weightDistribution = weightDistribution
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                ) {
                    CustomUndoBar(
                        visible = lastDeletedItems.isNotEmpty(),
                        message = when (lastDeletedItems.size) {
                            0 -> ""
                            1 -> "Elemento eliminado"
                            else -> "Elementos eliminados"
                        },
                        onUndo = {
                            materialViewModel.restoreLastDeletedItems()
                        },
                        onTimeout = {
                            materialViewModel.dismissUndoAction()
                        },
                        durationMillis = undoDuration.toLong()
                    )
                }
            }
        }
    }
}
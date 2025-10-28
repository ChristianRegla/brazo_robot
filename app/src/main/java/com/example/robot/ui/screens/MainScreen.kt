package com.example.robot.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.robot.ui.components.RobotChart
import com.example.robot.ui.components.RobotTable
import com.example.robot.ui.navigation.TabScreen
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.ui.components.AnimatedNavigationBarItem
import com.example.robot.ui.components.ConfirmationDialog
import com.example.robot.ui.components.CustomUndoBar
import com.example.robot.ui.navigation.tabs
import com.example.robot.ui.theme.RedAlert
import com.example.robot.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

private const val INITIALPAGE= 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToSettings: () -> Unit,
    settingsViewModel: SettingsViewModel
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val errorColor = MaterialTheme.colorScheme.error

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

    val loadingComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val noInternetComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.no_internet)
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

    if (showClearConfirmationDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.confirmarEliminacionTotalTitulo),
            text = stringResource(R.string.confirmacionEliminacionTotal),
            onConfirm = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                materialViewModel.deleteAllMateriales(undoDuration.toLong())
            },
            onDismiss = { showClearConfirmationDialog = false }
        )
    }

    if (showDeleteSelectedConfirmationDialog) {
        ConfirmationDialog(
            title = stringResource(id = R.string.confirmarEliminacionSeleccionadosTitulo),
            text = stringResource(id = R.string.confirmacionEliminarSeleccionados),
            onConfirm = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                materialViewModel.deleteSelectedItems(undoDuration.toLong())
            },
            onDismiss = { showDeleteSelectedConfirmationDialog = false }
        )
    }

    if (itemDetalle != null) {
        ModalBottomSheet(
            onDismissRequest = { itemDetalle = null },
            sheetState = sheetState,
            containerColor = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalles del Material",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))

                Text("ID: ${itemDetalle!!.id}")
                Text("Color: ${itemDetalle!!.color}")
                Text("Peso: ${itemDetalle!!.pesoGramos} g")
                Text("Es Metal: ${if (itemDetalle!!.esMetal) "Sí" else "No"}")
                Text("Categoría: ${itemDetalle!!.categoria}")

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        materialViewModel.deleteSingleMaterial(itemDetalle!!, undoDuration.toLong())
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                itemDetalle = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedAlert,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar este item")
                }
                Spacer(Modifier.height(16.dp))
            }
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
                if (selectedItems.isEmpty()) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = tabs[pagerState.currentPage].title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onGoHome) {
                                Icon(
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = stringResource(R.string.Inicio),
                                    tint = primaryColor
                                )
                            }
                        },
                        actions = {
                            Box {
                                IconButton(onClick = { showOptionsMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = stringResource(R.string.mas),
                                        tint = primaryColor
                                    )
                                }
                                DropdownMenu(
                                    expanded = showOptionsMenu,
                                    onDismissRequest = { showOptionsMenu = false },
                                    modifier = Modifier.background(surfaceColor)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.configuracion_titulo)) },
                                        onClick = {
                                            onGoToSettings()
                                            showOptionsMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Settings,
                                                contentDescription = stringResource(R.string.configuracion_titulo),
                                                tint = primaryColor
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.acerca_de_titulo)) },
                                        onClick = {
                                            onGoToAbout() // Navega a AboutScreen
                                            showOptionsMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = stringResource(R.string.acerca_de_titulo),
                                                tint = primaryColor
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.vaciar_lista)) },
                                        onClick = {
                                            if (confirmDeleteAll) {
                                                showClearConfirmationDialog = true
                                            } else {
                                                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                materialViewModel.deleteAllMateriales(undoDuration.toLong())
                                            }
                                            showOptionsMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = stringResource(R.string.vaciar_lista),
                                                tint = errorColor
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = surfaceColor
                        ),
                        modifier = Modifier.clip(RoundedCornerShape(
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        ))
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = pluralStringResource(
                                    id = R.plurals.seleccionados,
                                    count = selectedItems.size,
                                    selectedItems.size
                                ),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { materialViewModel.deselectAllItems() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.limpiar_seleccion),
                                    tint = primaryColor
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (confirmDeleteSelected) {
                                    showDeleteSelectedConfirmationDialog = true
                                } else {
                                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    materialViewModel.deleteSelectedItems(undoDuration.toLong())
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.eliminar_seleccionados),
                                    tint = primaryColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = surfaceColor
                        ),
                        modifier = Modifier.clip(RoundedCornerShape(
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        ))
                    )
                }
            },
            bottomBar = {
                val itemCount = tabs.size

                val indicatorWidth = 80.dp
                val indicatorHeight = 32.dp
                val indicatorVerticalPadding = 12.dp

                val density = LocalDensity.current

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = surfaceColor,
                        shadowElevation = 6.dp
                    ) {
                        BoxWithConstraints {
                            val screenWidthPx = with(density) { maxWidth.toPx() }
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
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(surfaceColor)
                            ) {
                                NavigationBar(
                                    containerColor = Color.Transparent
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
                                            contentDescription = "Ir a ${'$'}{tab.title}"
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
                                            color = primaryColor.copy(alpha = 0.25f),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
        ) { innerPadding ->
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = backgroundBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    LottieAnimation(
                                        composition = loadingComposition,
                                        progress = { loadingProgress },
                                        modifier = Modifier.size(150.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.cargando),
                                        color = primaryColor,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }

                            !isConnected -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { materialViewModel.fetchMateriales() },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    LottieAnimation(
                                        composition = noInternetComposition,
                                        progress = { noInternetProgress },
                                        modifier = Modifier.size(180.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.sinConexion),
                                        color = primaryColor,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = stringResource(R.string.reintentar),
                                        color = primaryColor,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            materiales.isEmpty() && lastDeletedItems.isEmpty() -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    LottieAnimation(
                                        composition = emptyListComposition,
                                        progress = { emptyListProgress },
                                        modifier = Modifier.size(180.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.noHayDatos),
                                        color = primaryColor,
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
                                            bottom = 12.dp
                                        ),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    when (tabs[pageIndex]) {
                                        is TabScreen.Table -> RobotTable(
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
                                            onSortClick = { column ->
                                                materialViewModel.updateSortColumn(column)
                                            },
                                            currentUnit = selectedUnit
                                        )

                                        is TabScreen.Chart -> RobotChart(
                                            materiales = materiales,
                                            scrollState = scrollState,
                                            currentUnit = selectedUnit
                                        )
                                    }
                                }
                            }
                        }
                    }
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
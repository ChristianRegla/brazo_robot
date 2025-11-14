package com.example.robot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.model.UnitType
import com.example.robot.ui.navigation.TabScreen
import com.example.robot.viewmodel.GeminiUiState
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.viewmodel.WeightStatistics

@Composable
fun MainScreenPagerContent(
    isLoading: Boolean,
    isConnected: Boolean,
    materiales: List<MaterialItem>,
    lastDeletedItems: List<MaterialItem>,
    loadingComposition: com.airbnb.lottie.LottieComposition?,
    loadingProgress: Float,
    noInternetComposition: com.airbnb.lottie.LottieComposition?,
    noInternetProgress: Float,
    emptyListComposition: com.airbnb.lottie.LottieComposition?,
    emptyListProgress: Float,
    pageIndex: Int,
    tabs: List<TabScreen>,
    materialViewModel: MaterialViewModel,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    selectedItems: Set<MaterialItem>,
    sortState: Pair<com.example.robot.viewmodel.SortableColumn, com.example.robot.viewmodel.SortDirection>?,
    onItemClick: (MaterialItem) -> Unit,
    onItemLongClick: (MaterialItem) -> Unit,
    onSortClick: (com.example.robot.viewmodel.SortableColumn) -> Unit,
    currentUnit: UnitType,
    scrollState: androidx.compose.foundation.ScrollState,
    onRetryConnection: () -> Unit,
    weightStatistics: WeightStatistics,
    weightDistribution: Map<String, Int>,
    geminiUiState: GeminiUiState
) {
    val primaryColor = MaterialTheme.colorScheme.primary

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
                    .clickable { onRetryConnection() },
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
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onSortClick = onSortClick,
                        currentUnit = currentUnit
                    )

                    is TabScreen.Chart -> RobotChart(
                        materiales = materiales,
                        scrollState = scrollState,
                        currentUnit = currentUnit,
                        weightStatistics = weightStatistics,
                        weightDistribution = weightDistribution,
                        viewModel = materialViewModel,
                        geminiUiState = geminiUiState
                    )
                }
            }
        }
    }
}
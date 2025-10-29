package com.example.robot.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.robot.R
import com.example.robot.ui.navigation.TabScreen
import kotlin.math.roundToInt

@Composable
fun MainScreenBottomAppBar(
    pagerState: androidx.compose.foundation.pager.PagerState,
    tabs: List<TabScreen>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onTabClick: (index: Int, currentTab: TabScreen) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
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
                    NavigationBar(containerColor = Color.Transparent) {
                        tabs.forEachIndexed { index, tab ->
                            AnimatedNavigationBarItem(
                                isSelected = pagerState.currentPage == index,
                                onClick = {
                                    onTabClick(index, tab)
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
                                color = primaryColor.copy(alpha = 0.25f),
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                }
            }
        }
    }
}
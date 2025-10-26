package com.example.robot.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.model.UnitType
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.viewmodel.SortDirection
import com.example.robot.viewmodel.SortableColumn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RobotTable(
    materialViewModel: MaterialViewModel,
    lazyListState: LazyListState,
    selectedItems: Set<MaterialItem>,
    sortState: Pair<SortableColumn, SortDirection>?,
    onItemClick: (MaterialItem) -> Unit,
    modifier: Modifier = Modifier,
    onItemLongClick: (MaterialItem) -> Unit,
    onSortClick: (SortableColumn) -> Unit,
    currentUnit: UnitType
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val haptic = LocalHapticFeedback.current
    val materiales by materialViewModel.filteredAndSortedMateriales.collectAsStateWithLifecycle()
    val headers = remember {
        listOf(
            "Color",
            "Peso",
            "¿Es Metal?",
            "Categoría"
        )
    }

    Surface(
        modifier = modifier,
        color = surfaceColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .background(surfaceColor),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            primaryColor.copy(alpha = 0.15f),
                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(top = 40.dp, bottom = 12.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    headers.forEachIndexed { index, header ->
                        val column = SortableColumn.entries[index]
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSortClick(column) }
                                .padding(horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AutoSizeText(
                                text = header,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            if (sortState?.first == column) {
                                Icon(
                                    imageVector = if (sortState.second == SortDirection.ASC) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = stringResource(id = R.string.columna_ordenada_por, header),
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 2.dp,
                    color = primaryColor.copy(alpha = 0.6f)
                )

                LazyColumn(state = lazyListState) {
                    itemsIndexed(
                        items = materiales,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        val isSelected = selectedItems.contains(item)
                        val shape = RoundedCornerShape(8.dp)

                        val targetBackgroundColor = when {
                            isSelected -> primaryColor.copy(alpha = 0.4f)
                            index % 2 == 0 -> Color.Transparent
                            else -> onSurfaceColor.copy(alpha = 0.05f)
                        }

                        val animatedBackgroundColor by animateColorAsState(
                            targetValue = targetBackgroundColor,
                            label = "RowBackgroundAnimation"
                        )
                        val rowModifier = if (isSelected) {
                            Modifier.border(
                                width = 2.dp,
                                color = primaryColor,
                                shape = shape
                            )
                        } else {
                            Modifier
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    color = animatedBackgroundColor,
                                    shape = shape
                                )
                                .then(rowModifier)
                                .clip(shape)
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        onItemClick(item)
                                    },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onItemLongClick(item)
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .animateItem(
                                    fadeInSpec = tween(
                                        durationMillis = 300,
                                        delayMillis = 30 * index
                                    )
                                )
                        ) {
                            Text(
                                text = item.color.ifBlank { "-" },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = onSurfaceColor,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = when (currentUnit) {
                                    UnitType.GRAMS -> stringResource(R.string.peso_valor_gr, item.pesoGramos)
                                    UnitType.KILOGRAMS -> stringResource(R.string.peso_valor_kg, item.pesoGramos * currentUnit.conversionFactor)
                                    UnitType.POUNDS -> stringResource(R.string.peso_valor_lb, item.pesoGramos * currentUnit.conversionFactor)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = onSurfaceColor,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (item.esMetal) "Sí" else "No",
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = onSurfaceColor,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = item.categoria.ifBlank { "-" },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = onSurfaceColor,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (index != materiales.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 1.dp,
                                color = outlineVariantColor
                            )
                        }
                    }
                }
            }

            FilterButton(
                viewModel = materialViewModel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            )

        }
    }
}

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle
) {
    var scaledTextStyle by remember { mutableStateOf(style) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    val minFontSize = 9.sp

    LaunchedEffect(text, style) {
        scaledTextStyle = style
    }

    Text(
        text = text,
        modifier = modifier.onGloballyPositioned { coords ->
            val newWidth = coords.size.width
            if (layoutWidth != newWidth) layoutWidth = newWidth
        },
        style = scaledTextStyle,
        softWrap = false,
        maxLines = 1,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth && scaledTextStyle.fontSize > minFontSize) {
                scaledTextStyle = scaledTextStyle.copy(
                    fontSize = scaledTextStyle.fontSize * 0.95f
                )
            }
        }
    )
}
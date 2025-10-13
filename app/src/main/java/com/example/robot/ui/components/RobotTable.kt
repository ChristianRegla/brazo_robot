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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.viewmodel.SortDirection
import com.example.robot.viewmodel.SortableColumn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RobotTable(
    headers: List<String>,
    materiales: List<MaterialItem>,
    lazyListState: LazyListState,
    viewModel: MaterialViewModel,
    selectedItems: Set<MaterialItem>,
    sortState: Pair<SortableColumn, SortDirection>?,
    onItemClick: (MaterialItem) -> Unit,
    modifier: Modifier = Modifier,
) {

    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        NeonBlue.copy(alpha = 0.15f),
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(vertical = 12.dp)
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
                            ) { viewModel.sortTable(column) }
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = header,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                        if (sortState?.first == column) {
                            Icon(
                                imageVector = if (sortState.second == SortDirection.ASC) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = stringResource(id = R.string.columna_ordenada_por, header),
                                tint = NeonBlue,
                                modifier = Modifier.size(16.dp).padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = NeonBlue.copy(alpha = 0.6f)
            )

            LazyColumn(state = lazyListState) {
                itemsIndexed(
                    items = materiales,
                    key = { _, item -> item.id }
                ) { index, item ->

                    val isSelected = selectedItems.contains(item)
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) NeonBlue.copy(alpha = 0.4f) else Color.Transparent,
                    )
                    val rowModifier = if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = NeonBlue,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Modifier
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(animatedBackgroundColor)
                            .then(rowModifier)
                            .combinedClickable(
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        onItemClick(item)
                                    }
                                },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onItemClick(item)
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
                            text = item.color,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${item.pesoGramos}g",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (item.esMetal) "Sí" else "No",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = item.categoria,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (index != materiales.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = Color(0xFFCCCCCC).copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}
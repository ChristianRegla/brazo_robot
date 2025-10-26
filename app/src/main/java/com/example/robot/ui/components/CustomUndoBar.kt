package com.example.robot.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomUndoBar(
    visible: Boolean,
    message: String,
    onUndo: () -> Unit,
    onTimeout: () -> Unit,
    durationMillis: Long = 4000
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary

    val dismissState = rememberDismissState()

    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMillis)
            if (dismissState.currentValue != DismissValue.DismissedToEnd &&
                dismissState.currentValue != DismissValue.DismissedToStart
            ) {
                onTimeout()
            }
        }
    }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == DismissValue.DismissedToEnd ||
            dismissState.currentValue == DismissValue.DismissedToStart
        ) {
            onTimeout()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
            background = {},
            dismissContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(8.dp)
                        .background(surfaceColor, shape = RoundedCornerShape(14.dp))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Deshacer",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = message,
                            color = onSurfaceColor,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "DESHACER",
                            color = primaryColor,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .clickable(enabled = visible) {
                                    onUndo()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        )
    }
}
package com.example.robot.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.RedAlert
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    icon: ImageVector = Icons.Default.Warning,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val errorColor = MaterialTheme.colorScheme.error
    val onErrorColor = MaterialTheme.colorScheme.onError

    var isConfirmEnabled by remember { mutableStateOf(false) }
    val fillProgress = remember { Animatable(0f) }

    var countdownText by remember { mutableStateOf(" (5)") }

    LaunchedEffect(Unit) {
        fillProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000)
        )
        isConfirmEnabled = true
    }

    LaunchedEffect(Unit) {
        delay(1000L)
        countdownText = " (4)"
        delay(1000L)
        countdownText = " (3)"
        delay(1000L)
        countdownText = " (2)"
        delay(1000L)
        countdownText = " (1)"
        delay(1000L)
        countdownText = ""
    }

    val disabledColor = errorColor.copy(alpha = 0.5f)
    val enabledColor = errorColor

    val buttonBrush = Brush.linearGradient(
        colorStops = arrayOf(
            fillProgress.value to enabledColor,
            fillProgress.value to disabledColor
        ),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Advertencia",
                    tint = errorColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = onSurfaceColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = onSurfaceColor.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryColor.copy(alpha = 0.7f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            color = onPrimaryColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(buttonBrush)
                            .clickable(enabled = isConfirmEnabled) {
                                onConfirm()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Confirmar$countdownText",
                            color = onErrorColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
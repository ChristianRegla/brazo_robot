package com.example.robot.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.robot.R
import com.example.robot.ui.theme.NeonBlue

@Composable
fun AnimatedStartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "buttonScale")

    val infiniteTransition = rememberInfiniteTransition(label = "infinite Transition")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 690f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, delayMillis = 500)
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.3f),
            Color.Transparent,
        ),
        start = Offset(shimmerTranslate - 100f, 0f),
        end = Offset(shimmerTranslate, 0f)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .widthIn(min = 200.dp)
            .height(50.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(25.dp),
                clip = false,
                ambientColor = primaryColor.copy(alpha = glowAlpha),
                spotColor = primaryColor.copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(25.dp))
            .background(primaryColor)
            .background(shimmerBrush)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.botonComenzar),
            color = onPrimaryColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
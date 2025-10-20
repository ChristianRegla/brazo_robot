package com.example.robot.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.robot.R
import com.example.robot.ui.components.AnimatedStartButton
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    val startAlpha = 0f
    val endAlpha = 1f
    val startTranslationY = 50f
    val endTranslationY = 0f

    var startAnimation by remember { mutableStateOf(false) }
    var startButtonAnimation by remember { mutableStateOf(false) }

    val animatedIconAlpha by animateFloatAsState(
        targetValue = if (startAnimation) endAlpha else startAlpha,
        animationSpec = tween(1000)
    )
    val animatedIconTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) endTranslationY else startTranslationY,
        animationSpec = tween(1000)
    )

    val animatedTitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) endAlpha else startAlpha,
        animationSpec = tween(500, delayMillis = 200)
    )
    val animatedTitleTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) endTranslationY else startTranslationY,
        animationSpec = tween(500, delayMillis = 200)
    )

    val animatedWelcomeTextAlpha by animateFloatAsState(
        targetValue = if (startAnimation) endAlpha else startAlpha,
        animationSpec = tween(500, delayMillis = 400)
    )
    val animatedWelcomeTextTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) endTranslationY else startTranslationY,
        animationSpec = tween(500, delayMillis = 400)
    )

    val animatedCardAlpha by animateFloatAsState(
        targetValue = if (startAnimation) endAlpha else startAlpha,
        animationSpec = tween(500, delayMillis = 600)
    )
    val animatedCardTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) endTranslationY else startTranslationY,
        animationSpec = tween(500, delayMillis = 600)
    )

    val animatedButtonAlpha by animateFloatAsState(
        targetValue = if (startButtonAnimation) endAlpha else startAlpha,
        animationSpec = tween(500)
    )
    val animatedButtonTranslationY by animateFloatAsState(
        targetValue = if (startButtonAnimation) endTranslationY else startTranslationY,
        animationSpec = tween(500)
    )

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(1000)
        startButtonAnimation = true
    }

    RobotTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                NightBlue,
                                SpaceGray,
                                DeepBlue
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, 1000f)
                        )
                    )
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Filled.Android,
                        contentDescription = stringResource(R.string.logoRobot),
                        tint = NeonBlue,
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer {
                                alpha = animatedIconAlpha
                                translationY = animatedIconTranslationY
                            }
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall,
                        color = NeonBlue,
                        modifier = Modifier.graphicsLayer {
                            alpha = animatedTitleAlpha
                            translationY = animatedTitleTranslationY
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.bienvenida),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.graphicsLayer {
                            alpha = animatedWelcomeTextAlpha
                            translationY = animatedWelcomeTextTranslationY
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SpaceGray,
                            contentColor = TextPrimary
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .graphicsLayer {
                                alpha = animatedCardAlpha
                                translationY = animatedCardTranslationY
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.integrantes),
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonBlue,
                                fontWeight = Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = NeonBlue.copy(alpha = 0.7f)
                            )

                            val alumnos = listOf(
                                stringResource(R.string.alumno1),
                                stringResource(R.string.alumno2)
                            )
                            alumnos.forEach { alumno ->
                                Text(
                                    text = alumno,
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedStartButton(
                        onClick = onStartClick,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .graphicsLayer {
                                alpha = animatedButtonAlpha
                                translationY = animatedButtonTranslationY
                            }
                    )

                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onStartClick = {})
}
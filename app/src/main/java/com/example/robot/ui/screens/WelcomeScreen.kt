package com.example.robot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
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
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Bienvenido!",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sistema de control de brazo robot",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onStartClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .width(220.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Comenzar",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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
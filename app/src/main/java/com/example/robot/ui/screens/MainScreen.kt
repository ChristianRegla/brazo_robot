package com.example.robot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.robot.ui.components.RobotTable
import com.example.robot.ui.theme.NightBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.RedAlert
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.ui.theme.RobotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onGoHome: () -> Unit,
    onExit: () -> Unit
) {

    val headers = listOf("Sensor", "Valor", "Estado")
    var rows by remember {
        mutableStateOf(
            listOf(
                listOf("Distancia", "20cm", "Activo"),
                listOf("Temperatura", "32°C", "Normal"),
                listOf("Luz", "Baja", "Alerta")
            )
        )
    }
    RobotTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Tablas del Robot",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onGoHome) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Inicio",
                                tint = NeonBlue
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onExit) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Salir",
                                tint = NeonBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SpaceGray
                    )
                )
            },
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
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    RobotTable(
                        headers = headers,
                        rows = rows,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Button(
                            onClick = {
                                // Agregar fila
                                rows = rows + listOf(listOf("Nuevo", "0", "Inactivo"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                        ) {
                            Text("Agregar fila")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Quitar última fila
                                if (rows.isNotEmpty()) {
                                    rows = rows.dropLast(1)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedAlert)
                        ) {
                            Text("Quitar fila")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(onGoHome = {}, onExit = {})
}
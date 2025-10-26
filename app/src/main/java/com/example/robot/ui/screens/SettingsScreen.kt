package com.example.robot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.robot.R
import com.example.robot.model.UnitType
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.SettingsViewModel
import com.example.robot.viewmodel.ThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val selectedUnit by settingsViewModel.unitType.collectAsStateWithLifecycle()
    val selectedTheme by settingsViewModel.themePreference.collectAsStateWithLifecycle()

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.background

    RobotTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.configuracion_titulo),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.volver_atras),
                                tint = primaryColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceColor
                    ),
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        clip = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.unidades_medida_peso),
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceColor,
                        contentColor = onSurfaceColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .selectableGroup()
                    ) {
                        UnitType.values().forEach { unitType ->
                            SettingRadioButtonRow(
                                text = unitType.displayName,
                                selected = (unitType == selectedUnit),
                                onClick = { settingsViewModel.setUnitType(unitType) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.apariencia_tema),
                    style = MaterialTheme.typography.titleMedium,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceColor,
                        contentColor = onSurfaceColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectableGroup()
                    ) {
                        ThemePreference.values().forEach { themePref ->
                            SettingRadioButtonRow(
                                text = when(themePref) {
                                    ThemePreference.SYSTEM -> stringResource(R.string.tema_sistema)
                                    ThemePreference.LIGHT -> stringResource(R.string.tema_claro)
                                    ThemePreference.DARK -> stringResource(R.string.tema_oscuro)
                                },
                                selected = (themePref == selectedTheme),
                                onClick = { settingsViewModel.setThemePreference(themePref) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRadioButtonRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val radioColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = radioColors
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
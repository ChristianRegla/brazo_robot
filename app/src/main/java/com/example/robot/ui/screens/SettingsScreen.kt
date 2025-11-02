package com.example.robot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.robot.R
import com.example.robot.model.UnitType
import com.example.robot.ui.components.ConfirmationDialog
import com.example.robot.ui.theme.RobotTheme
import com.example.robot.viewmodel.SettingsViewModel
import com.example.robot.viewmodel.ThemePreference
import com.example.robot.viewmodel.UndoDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onGoToAbout: () -> Unit
) {
    val selectedUnit by settingsViewModel.unitType.collectAsStateWithLifecycle()
    val selectedTheme by settingsViewModel.themePreference.collectAsStateWithLifecycle()
    val hapticEnabled by settingsViewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val selectedUndoDuration by settingsViewModel.undoDurationMillis.collectAsStateWithLifecycle()
    val confirmDeleteSelected by settingsViewModel.confirmDeleteSelected.collectAsStateWithLifecycle()
    val confirmDeleteAll by settingsViewModel.confirmDeleteAll.collectAsStateWithLifecycle()
    val modoAutomatico by settingsViewModel.modoAutomatico.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background

    if (showResetDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.confirmar_restablecer_titulo),
            text = stringResource(R.string.confirmar_restablecer_texto),
            icon = Icons.Default.Warning,
            onConfirm = {
                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                settingsViewModel.resetToDefaults()
            },
            onDismiss = { showResetDialog = false }
        )
    }

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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SectionCard(title = "Control del Robot") {
                    SettingSwitchRow(
                        text = "Modo Automático",
                        description = "Si está activo, el robot acomodará el material inmediatamente. Si no, la app pedirá confirmación.",
                        checked = modoAutomatico,
                        onCheckedChange = { nuevoEstado ->
                            settingsViewModel.setModoAutomatico(nuevoEstado)
                        }
                    )
                }
                SectionCard(title = stringResource(R.string.unidades_medida_peso)) {
                    UnitType.entries.forEach { unitType ->
                        SettingRadioButtonRow(
                            text = unitType.displayName,
                            selected = (unitType == selectedUnit),
                            onClick = { settingsViewModel.setUnitType(unitType) }
                        )
                    }
                }

                SectionCard(title = stringResource(R.string.apariencia_tema)) {
                    ThemePreference.entries.forEach { themePref ->
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

                SectionCard(title = stringResource(R.string.comportamiento_titulo)) {
                    SettingSwitchRow(
                        text = stringResource(R.string.feedback_haptico),
                        description = stringResource(R.string.feedback_haptico_desc),
                        checked = hapticEnabled,
                        onCheckedChange = { settingsViewModel.setHapticFeedbackEnabled(it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )

                    SettingRadioGroupSection(
                        title = stringResource(R.string.duracion_deshacer),
                        options = mapOf(
                            UndoDuration.SHORT to stringResource(R.string.duracion_corta),
                            UndoDuration.MEDIUM to stringResource(R.string.duracion_media),
                            UndoDuration.LONG to stringResource(R.string.duracion_larga)
                        ),
                        selectedOption = selectedUndoDuration,
                        onOptionSelected = { settingsViewModel.setUndoDurationMillis(it) }
                    )
                }

                SectionCard(title = stringResource(R.string.confirmaciones_titulo)) {
                    SettingSwitchRow(
                        text = stringResource(R.string.confirmar_eliminar_seleccionados),
                        checked = confirmDeleteSelected,
                        onCheckedChange = { settingsViewModel.setConfirmDeleteSelected(it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )
                    SettingSwitchRow(
                        text = stringResource(R.string.confirmar_vaciar_lista),
                        checked = confirmDeleteAll,
                        onCheckedChange = { settingsViewModel.setConfirmDeleteAll(it) }
                    )
                }

                SectionCard(title = stringResource(R.string.restablecer_valores_titulo)) {
                    SettingClickableRow(
                        text = stringResource(R.string.restablecer_configuracion),
                        description = stringResource(R.string.restablecer_configuracion_desc),
                        icon = Icons.Default.Restore,
                        onClick = { showResetDialog = true }
                    )
                }

                SectionCard(title = stringResource(R.string.informacion_titulo)) {
                    SettingClickableRow(
                        text = stringResource(R.string.acerca_de_titulo),
                        icon = Icons.Default.Info,
                        onClick = onGoToAbout
                    )
                }
            }
        }
    }
}

@Composable
fun SettingClickableRow(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (description == null) 48.dp else 64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Padding vertical para los items internos
                    .selectableGroup(), // Necesario para RadioButtons
                content = content
            )
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
                role = Role.RadioButton,
                interactionSource = remember { MutableInteractionSource() },
                indication = null

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

@Composable
fun SettingSwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (description == null) 48.dp else 64.dp)
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = switchColors
        )
    }
}

@Composable
fun SettingRadioGroupSection(
    title: String,
    options: Map<Int, String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        options.forEach { (value, text) ->
            SettingRadioButtonRow(
                text = text,
                selected = (selectedOption == value),
                onClick = { onOptionSelected(value) }
            )
        }
    }
}
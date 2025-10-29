package com.example.robot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.robot.R
import com.example.robot.model.MaterialItem

@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopAppBar(
    selectedItems: Set<MaterialItem>,
    currentPageTitle: String,
    onGoHomeClick: () -> Unit,
    onDeselectAllClick: () -> Unit,
    onDeleteSelectedClick: () -> Unit,
    showOptionsMenu: Boolean,
    onDismissOptionsMenu: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToAbout: () -> Unit,
    onClearListClick: () -> Unit,
    onOptionsIconClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val errorColor = MaterialTheme.colorScheme.error

    val topAppBarModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
    )

    if (selectedItems.isEmpty()) {
        CenterAlignedTopAppBar(
            title = { Text(
                text = currentPageTitle,
                style = MaterialTheme.typography.titleLarge
            ) },
            navigationIcon = { IconButton(onClick = onGoHomeClick) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(R.string.Inicio),
                    tint = primaryColor
                )
            } },
            actions = {
                Box {
                    IconButton(onClick = onOptionsIconClick) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.mas),
                            tint = primaryColor
                        )
                    }
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = onDismissOptionsMenu,
                        modifier = Modifier.background(surfaceColor)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.configuracion_titulo)) },
                            onClick = {
                                onGoToSettings()
                                onDismissOptionsMenu()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.configuracion_titulo),
                                    tint = primaryColor
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.acerca_de_titulo)) },
                            onClick = {
                                onGoToAbout()
                                onDismissOptionsMenu()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = stringResource(R.string.acerca_de_titulo),
                                    tint = primaryColor
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.vaciar_lista)) },
                            onClick = {
                                onClearListClick()
                                onDismissOptionsMenu()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.vaciar_lista),
                                    tint = errorColor
                                )
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor),
            modifier = topAppBarModifier
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.seleccionados,
                        count = selectedItems.size,
                        selectedItems.size
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onDeselectAllClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.limpiar_seleccion),
                        tint = primaryColor
                    )
                }
            },
            actions = {
                IconButton(onClick = onDeleteSelectedClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.eliminar_seleccionados),
                        tint = primaryColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor),
            modifier = topAppBarModifier
        )
    }
}
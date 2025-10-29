package com.example.robot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.ui.theme.RedAlert
import com.example.robot.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailsBottomSheet(
    item: MaterialItem?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDeleteClick: (MaterialItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val surfaceColor = MaterialTheme.colorScheme.surface

    if (item != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Detalles del Material",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))

                Text("ID: ${item.id}")
                Text("Color: ${item.color.ifBlank { "-" }}")
                Text("Peso: ${item.pesoGramos} g")
                Text("Es Metal: ${if (item.esMetal) "Sí" else "No"}")
                Text("Categoría: ${item.categoria.ifBlank { "-" }}")

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        onDeleteClick(item)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedAlert,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar") // Mover a strings.xml
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar este item")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
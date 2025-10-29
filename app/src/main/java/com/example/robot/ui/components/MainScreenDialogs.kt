package com.example.robot.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.example.robot.R

// ... (Al final del archivo MainScreen.kt) ...

@Composable
fun MainScreenDialogs(
    showClearConfirmation: Boolean,
    onClearConfirm: () -> Unit,
    onClearDismiss: () -> Unit,
    showDeleteSelectedConfirmation: Boolean,
    onDeleteSelectedConfirm: () -> Unit,
    onDeleteSelectedDismiss: () -> Unit,
    hapticEnabled: Boolean,
    haptic: HapticFeedback
) {
    val currentHaptic = LocalHapticFeedback.current

    if (showClearConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.confirmarEliminacionTotalTitulo),
            text = stringResource(R.string.confirmacionEliminacionTotal),
            onConfirm = {
                if (hapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClearConfirm()
            },
            onDismiss = onClearDismiss
        )
    }

    if (showDeleteSelectedConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.confirmarEliminacionSeleccionadosTitulo),
            text = stringResource(id = R.string.confirmacionEliminarSeleccionados),
            onConfirm = {
                if (hapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDeleteSelectedConfirm()
            },
            onDismiss = onDeleteSelectedDismiss
        )
    }
}
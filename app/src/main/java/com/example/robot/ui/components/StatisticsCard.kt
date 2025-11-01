package com.example.robot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.robot.R
import com.example.robot.model.UnitType
import com.example.robot.viewmodel.WeightStatistics
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatisticsCard(
    materialCount: Int,
    statistics: WeightStatistics,
    currentUnit: UnitType
) {
    val numberFormatter = remember(currentUnit) {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            if (currentUnit == UnitType.GRAMS) {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            } else {
                maximumFractionDigits = 4
                minimumFractionDigits = 4
            }
        }
    }

    val conversionFactor = currentUnit.conversionFactor
    val varianceConversionFactor = conversionFactor * conversionFactor

    val weightUnit = when (currentUnit) {
        UnitType.GRAMS -> stringResource(R.string.gr_abreviacion)
        UnitType.KILOGRAMS -> stringResource(R.string.kg_abreviacion)
        UnitType.POUNDS -> stringResource(R.string.lb_abreviacion)
    }
    val varianceUnit = "${weightUnit}²"

    @Composable
    fun StatRow(label: String, value: String, unit: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen de Datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            StatRow(
                label = "Total de Muestras:",
                value = materialCount.toString(),
                unit = "items"
            )

            StatRow(
                label = "Media (Promedio):",
                value = numberFormatter.format(statistics.mean * conversionFactor),
                unit = weightUnit
            )
            StatRow(
                label = "Varianza:",
                value = numberFormatter.format(statistics.variance * varianceConversionFactor),
                unit = varianceUnit
            )
            StatRow(
                label = "Desv. Estándar:",
                value = numberFormatter.format(statistics.stdDev * conversionFactor),
                unit = weightUnit
            )
        }
    }
}
package com.example.robot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.robot.ui.theme.NeonBlue

@Composable
fun RobotTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header row
            Row(Modifier.fillMaxWidth()) {
                headers.forEach { header ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f), RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = header,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 2.dp,
                color = NeonBlue.copy(alpha = 0.6f)
            )

            Box(
                modifier = modifier
            ) {
                LazyColumn {
                    itemsIndexed(rows) { index, row ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            row.forEach { cell ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = cell,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        if (index != rows.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 2.dp,
                                color = Color(0xFFCCCCCC).copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.robot.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.SpaceGray


@Composable
fun RobotChart(
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    val points = rows.mapIndexed { index, row ->
        val peso = row[1].replace("g", "").toFloatOrNull() ?: 0f
        Point(index.toFloat(), peso)
    }

    val lineStyle = LineStyle(
        lineType = LineType.SmoothCurve(isDotted = false),
        color = NeonBlue,
        width = 4f,
        style = Stroke(width = 4f)
    )

    val line = Line(
        dataPoints = points,
        lineStyle = lineStyle
    )

    val linePlotData = LinePlotData(
        plotType = PlotType.Line,
        lines = listOf(line)
    )

    val lineChartData = LineChartData(
        linePlotData = linePlotData,
        backgroundColor = SpaceGray
    )

    LineChart(
        lineChartData = lineChartData,
        modifier = modifier.fillMaxWidth().height(220.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RobotChartPreview() {
    val fakeRows = listOf(
        listOf("Rojo", "50g", "Sí", "Botella"),
        listOf("Verde", "100g", "No", "Plástico"),
        listOf("Azul", "800g", "Sí", "Botella")
    )
    RobotChart(
        rows = fakeRows,
        modifier = Modifier.fillMaxWidth()
    )
}
package com.example.robot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.robot.ui.theme.CyanAccent
import com.example.robot.ui.theme.GreenSensor
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.RedAlert
import com.example.robot.ui.theme.SpaceGray


@Composable
fun RobotChart(
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier.fillMaxWidth().verticalScroll(scrollState).then(modifier)
    ) {
        Text(
            text = "Peso de materiales",
            style = MaterialTheme.typography.titleMedium,
            color = NeonBlue,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        LineChartPesos(rows)
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Cantidad de materiales por categoría",
            style = MaterialTheme.typography.titleMedium,
            color = NeonBlue,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        BarChartCategorias(rows)
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Proporción de metales vs no metales",
            style = MaterialTheme.typography.titleMedium,
            color = NeonBlue,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        PieChartMetales(rows)
    }
}

@Composable
fun LineChartPesos(rows: List<List<String>>) {
    val points = rows.mapIndexed { index, row ->
        val peso = row.getOrNull(1)?.replace("g", "")?.toFloatOrNull() ?: 0f
        Point(index.toFloat(), peso)
    }
    val lineStyle = LineStyle(
        lineType = LineType.SmoothCurve(isDotted = false),
        color = NeonBlue,
        width = 4f
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
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

@Composable
fun BarChartCategorias(rows: List<List<String>>) {
    val counts = rows.groupingBy { it.getOrNull(3) ?: "Desconocido" }.eachCount()
    val barColors = listOf(NeonBlue, CyanAccent, GreenSensor, RedAlert)
    val bars = counts.entries.mapIndexed { idx, entry ->
        BarData(
            point = Point(idx.toFloat(), entry.value.toFloat()),
            label = entry.key,
            color = barColors[idx % barColors.size]
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(bars.size - 1)
        .bottomPadding(16.dp)
        .axisLabelAngle(0f)
        .labelData { index -> bars.getOrNull(index)?.label ?: "" }
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .build()

    val maxRange = (counts.values.maxOrNull() ?: 0).toFloat()

    val yAxisData = AxisData.Builder()
        .steps(4)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .labelData { value ->
            val finalValue = if (value.toFloat() >= maxRange) maxRange else value.toFloat()
            "%.0f".format(finalValue)
        }
        .build()


    val barChartData = BarChartData(
        chartData = bars,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = SpaceGray,
        barStyle = BarStyle(
            barWidth = 50.dp,
            cornerRadius = 4.dp
        ),
        horizontalExtraSpace = 20.dp
    )
    BarChart(
        barChartData = barChartData,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
fun PieChartMetales(rows: List<List<String>>) {
    val metalCount = rows.count { it.getOrNull(2)?.contains("si", ignoreCase = true) == true }
    val nonMetalCount = rows.size - metalCount
    val pieSlices = listOf(
        PieChartData.Slice(
            label = "Metales",
            value = metalCount.toFloat(),
            color = GreenSensor
        ),
        PieChartData.Slice(
            label = "No Metales",
            value = nonMetalCount.toFloat(),
            color = RedAlert
        )
    )
    val pieChartData = PieChartData(
        slices = pieSlices,
        plotType = PlotType.Pie
    )
    val pieChartConfig = PieChartConfig(
        showSliceLabels = true,
        sliceLabelTextColor = Color.White,
        sliceLabelTextSize = 14.sp,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        labelColor = Color.White,
        backgroundColor = SpaceGray
    )
    PieChart(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        pieChartData = pieChartData,
        pieChartConfig = pieChartConfig,
        onSliceClick = {}
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
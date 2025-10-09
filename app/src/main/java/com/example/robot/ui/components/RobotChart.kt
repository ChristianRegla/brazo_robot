package com.example.robot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import android.graphics.Typeface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.res.ResourcesCompat
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
import com.example.robot.R
import com.example.robot.ui.theme.BorderGray
import com.example.robot.ui.theme.CyanAccent
import com.example.robot.ui.theme.DeepBlue
import com.example.robot.ui.theme.GreenSensor
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.NightBlue
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
            text = stringResource(R.string.pesoMateriales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        LineChartPesos(rows)
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.cantidadMateriales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        BarChartCategorias(rows)
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.proporcionMetales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
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
    val desconocido = stringResource(R.string.desconocido)
    val counts = rows.groupingBy { it.getOrNull(3) ?: desconocido }.eachCount()
    val barColors = listOf(NeonBlue, CyanAccent, GreenSensor, RedAlert)
    val sortedCounts = counts.entries.sortedBy { it.key }
    val bars = sortedCounts.mapIndexed { idx, entry ->
        BarData(
            point = Point(idx.toFloat(), entry.value.toFloat()),
            label = entry.key,
            color = barColors[idx % barColors.size]
        )
    }

    val context = LocalContext.current
    val customTypceFace = remember(context) {
        ResourcesCompat.getFont(context, R.font.roboto_mono_regular)
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .bottomPadding(16.dp)
        .axisLabelAngle(0f)
        .labelData { index -> bars.getOrNull(index)?.label ?: "" }
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .startDrawPadding(50.dp)
        .typeFace(customTypceFace ?: Typeface.DEFAULT)
        .build()

    val maxCount = counts.values.maxOrNull() ?: 0
    val yAxisSteps = if (maxCount <= 5) maxCount else 4

    val yAxisData = AxisData.Builder()
        .steps(yAxisSteps)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .labelData { value -> "%.0f".format(value.toFloat()) }
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
    val metalCount = rows.count { it.getOrNull(2)?.contains("sí", ignoreCase = true) == true }
    val nonMetalCount = rows.size - metalCount
    val totalCount = (metalCount + nonMetalCount).toFloat()

    var selectedSlice by remember { mutableStateOf<PieChartData.Slice?>(null) }

    var showDialog by remember { mutableStateOf(false) }

    val pieSlices = if (totalCount == 0f) {
        emptyList()
    } else {
        listOf(
            PieChartData.Slice(
                label = stringResource(R.string.metales),
                value = metalCount.toFloat(),
                color = CyanAccent
            ),
            PieChartData.Slice(
                label = stringResource(R.string.noMetales),
                value = nonMetalCount.toFloat(),
                color = NeonBlue
            )
        )
    }

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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            pieChartData = pieChartData,
            pieChartConfig = pieChartConfig,
            onSliceClick = { slice ->
                selectedSlice = slice
                showDialog = true
            }
        )

        if (showDialog && selectedSlice != null) {
            SliceDetailDialog(
                slice = selectedSlice!!,
                totalValue = totalCount,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun SliceDetailDialog(
    slice: PieChartData.Slice,
    totalValue: Float,
    onDismiss: () -> Unit
) {
    val percentage = (slice.value / totalValue) * 100

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slice.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = slice.color
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cantidad: ${slice.value.toInt()}",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Porcentaje: ${"%.1f".format(percentage)}%",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
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
package com.example.robot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.CornerRadius
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
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.robot.R
import com.example.robot.model.MaterialItem
import com.example.robot.ui.theme.CyanAccent
import com.example.robot.ui.theme.GreenSensor
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.RedAlert
import com.example.robot.ui.theme.SpaceGray

@Composable
fun RobotChart(
    materiales: List<MaterialItem>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .then(modifier)
    ) {
        Text(
            text = stringResource(R.string.proporcionMetales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Card(
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceGray),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            PieChartMetales(materiales)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.proporcionColores),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Card(
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceGray),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            PieChartColores(materiales)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.cantidadMateriales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Card(
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceGray),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            BarChartCategorias(materiales)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.pesoMateriales),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        Card(
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceGray),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            LineChartPesos(materiales)
        }
    }
}

@Composable
fun LineChartPesos(materiales: List<MaterialItem>) {
    val points = materiales.mapIndexed { index, item ->
        Point(index.toFloat(), item.pesoGramos.toFloat())
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(60.dp)
        .steps(points.size - 1)
        .labelData { i ->
            if (points.size > 10 && i % 2 != 0) {
                ""
            } else {
                (i + 1).toString()
            }
        }
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .build()

    val maxWeight = points.maxOfOrNull { it.y } ?: 1f

    val yAxisData = AxisData.Builder()
        .steps(4)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .labelData { value ->
            val scale = maxWeight / 4f
            val labelValue = scale * value
            "%.0f g".format(labelValue)
        }
        .build()

    val lineStyle = LineStyle(
        lineType = LineType.SmoothCurve(isDotted = false),
        color = NeonBlue,
        width = 4f
    )

    val intersectionPoint = IntersectionPoint(
        color = NeonBlue,
        radius = 6.dp,
        alpha = 1.0f
    )

    val selectionHighlightPopUp = SelectionHighlightPopUp(
        backgroundColor = NeonBlue,
        backgroundAlpha = 0.9f,
        backgroundCornerRadius = CornerRadius(8f),
        paddingBetweenPopUpAndPoint = 12.dp,
        labelSize = 14.sp,
        labelColor = Color.White,
        popUpLabel = { x, y ->
            "Índice: ${x.toInt() + 1}\n Peso: ${"%.1f".format(y)}g"
        }
    )

    val line = Line(
        dataPoints = points,
        lineStyle = lineStyle,
        intersectionPoint = intersectionPoint,
        selectionHighlightPoint = SelectionHighlightPoint(),
        selectionHighlightPopUp = selectionHighlightPopUp
    )
    val linePlotData = LinePlotData(
        plotType = PlotType.Line,
        lines = listOf(line)
    )
    val lineChartData = LineChartData(
        linePlotData = linePlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = Color.Gray.copy(alpha = 0.5f)),
        backgroundColor = Color.Transparent,

    )
    LineChart(
        lineChartData = lineChartData,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )
}

@Composable
fun BarChartCategorias(materiales: List<MaterialItem>) {
    val desconocido = stringResource(R.string.desconocido)
    val counts = materiales.groupingBy { it.categoria.ifEmpty { desconocido } }.eachCount()
    val barColors = listOf(NeonBlue, CyanAccent)

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
        .axisStepSize(70.dp)
        .bottomPadding(16.dp)
        .axisLabelAngle(0f)
        .labelData { index -> (index + 1).toString() }
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .startDrawPadding(50.dp)
        .typeFace(customTypceFace ?: Typeface.DEFAULT)
        .build()

    val maxCount = counts.values.maxOrNull() ?: 1
    val yAxisIntervals = if (maxCount < 5) maxCount else 4

    val yAxisData = AxisData.Builder()
        .steps(yAxisIntervals)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(NeonBlue)
        .axisLabelColor(Color.White)
        .labelData { value ->
            val scale = maxCount.toFloat() / yAxisIntervals
            val labelValue = scale * value
            "%.0f".format(labelValue)
        }
        .build()

    val barChartData = BarChartData(
        chartData = bars,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.Transparent,
        barStyle = BarStyle(
            barWidth = 40.dp,
            cornerRadius = 4.dp
        ),
        horizontalExtraSpace = 20.dp
    )

    Column(modifier = Modifier.padding(16.dp)) {
        BarChart(
            barChartData = barChartData,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bars.forEachIndexed { index, barData ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1}.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(barData.color, RoundedCornerShape(2.dp))
                    )
                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = barData.label,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PieChartMetales(materiales: List<MaterialItem>) {
    val metalCount = materiales.count { it.esMetal }
    val nonMetalCount = materiales.size - metalCount
    val totalCount = materiales.size.toFloat()

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
        backgroundColor = Color.Transparent,
        isAnimationEnable = true
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            modifier = Modifier.size(220.dp),
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
fun PieChartColores(materiales: List<MaterialItem>) {
    val colorCounts = materiales.groupingBy { it.color }.eachCount()
    val totalCount = materiales.size.toFloat()

    var selectedSlice by remember { mutableStateOf<PieChartData.Slice?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val colorMap = mapOf(
        "Rojo" to RedAlert,
        "Verde" to GreenSensor,
        "Azul" to NeonBlue,
        "No hay color" to Color.Gray
    )

    val pieSlices = if (totalCount == 0f) {
        emptyList()
    } else {
        colorCounts.map { (colorName, count) ->
            PieChartData.Slice(
                label = colorName,
                value = count.toFloat(),
                color = colorMap[colorName] ?: Color.Magenta
            )
        }
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
        backgroundColor = Color.Transparent,
        isAnimationEnable = true,
        animationDuration = 800
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pieSlices.isEmpty()) {
            Text(
                text = stringResource(R.string.noHayDatos),
                modifier = Modifier.padding(32.dp),
                color = Color.White
            )
        } else {
            PieChart(
                modifier = Modifier.size(220.dp),
                pieChartData = pieChartData,
                pieChartConfig = pieChartConfig,
                onSliceClick = { slice ->
                    selectedSlice = slice
                    showDialog = true
                }
            )
        }

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
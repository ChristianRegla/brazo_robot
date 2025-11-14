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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.robot.model.UnitType
import com.example.robot.ui.theme.GreenSensor
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.RedAlert
import com.example.robot.viewmodel.GeminiUiState
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.viewmodel.WeightStatistics
import kotlin.math.roundToInt

private enum class ChartType(val title: String) {
    METALS("Proporción de Metales"),
    COLORS("Proporción de Colores"),
    CATEGORIES("Cantidad por Categoría"),
    DISTRIBUTION("Distribución de Peso"),
    WEIGHT_LINE("Peso (Gráfico de Línea)")
}

@Composable
fun RobotChart(
    materiales: List<MaterialItem>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    currentUnit: UnitType,
    weightStatistics: WeightStatistics,
    weightDistribution: Map<String, Int>,
    viewModel: MaterialViewModel,
    geminiUiState: GeminiUiState
) {
    var chartToShow by remember { mutableStateOf<ChartType?>(null) }

    var showGeminiDialog by remember { mutableStateOf(false) }

    if (showGeminiDialog) {
        GeminiSummaryDialog(
            uiState = geminiUiState,
            onDismiss = {
                showGeminiDialog = false
            },
            onRegenerate = {
                viewModel.forceGenerateGeminiSummary(materiales)
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(modifier)
    ) {
        StatisticsCard(
            materialCount = materiales.size,
            statistics = weightStatistics,
            currentUnit = currentUnit
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Análisis Gráfico",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val cardModifier = Modifier.weight(1f)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ChartNavigationCard(
                    title = ChartType.METALS.title,
                    icon = Icons.Default.PieChart,
                    onClick = { chartToShow = ChartType.METALS },
                    modifier = cardModifier
                )
                ChartNavigationCard(
                    title = ChartType.COLORS.title,
                    icon = Icons.Default.Palette,
                    onClick = { chartToShow = ChartType.COLORS },
                    modifier = cardModifier
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ChartNavigationCard(
                    title = ChartType.CATEGORIES.title,
                    icon = Icons.Default.BarChart,
                    onClick = { chartToShow = ChartType.CATEGORIES },
                    modifier = cardModifier
                )
                ChartNavigationCard(
                    title = ChartType.DISTRIBUTION.title,
                    icon = Icons.Default.Equalizer,
                    onClick = { chartToShow = ChartType.DISTRIBUTION },
                    modifier = cardModifier
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ChartNavigationCard(
                    title = ChartType.WEIGHT_LINE.title,
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    onClick = { chartToShow = ChartType.WEIGHT_LINE },
                    modifier = cardModifier
                )
                Spacer(modifier = cardModifier)
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                showGeminiDialog = true
                viewModel.generateGeminiSummary(materiales)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Generar Resumen con IA",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
    }

    chartToShow?.let { currentChart ->
        ChartDetailDialog(
            title = currentChart.title,
            onDismiss = { chartToShow = null }
        ) {
            when (currentChart) {
                ChartType.METALS -> PieChartMetales(materiales = materiales)
                ChartType.COLORS -> PieChartColores(materiales = materiales)
                ChartType.CATEGORIES -> BarChartCategorias(materiales = materiales)
                ChartType.DISTRIBUTION -> BarChartDistribucion(weightDistribution = weightDistribution)
                ChartType.WEIGHT_LINE -> LineChartPesos(materiales = materiales, currentUnit = currentUnit)
            }
        }
    }
}

@Composable
fun LineChartPesos(materiales: List<MaterialItem>, currentUnit: UnitType) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    val grAbreviacion = stringResource(R.string.gr_abreviacion)
    val kgAbreviacion = stringResource(R.string.kg_abreviacion)
    val lbAbreviacion = stringResource(R.string.lb_abreviacion)

    val points = materiales.mapIndexed { index, item ->
        Point(index.toFloat(), item.pesoGramos * currentUnit.conversionFactor)
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
        .axisLineColor(primaryColor)
        .axisLabelColor(onSurfaceColor)
        .build()

    val maxWeight = points.maxOfOrNull { it.y } ?: 1f
    val yAxisIntervals = 4

    val yAxisData = AxisData.Builder()
        .steps(yAxisIntervals)
        .labelAndAxisLinePadding(40.dp)
        .axisLineColor(primaryColor)
        .axisLabelColor(onSurfaceColor)
        .labelData { value ->
            val scale = maxWeight / yAxisIntervals
            val labelValue = scale * value
            when (currentUnit) {
                UnitType.GRAMS -> "${labelValue.roundToInt()} $grAbreviacion"
                UnitType.KILOGRAMS -> "%.2f $kgAbreviacion".format(labelValue)
                UnitType.POUNDS -> "%.2f $lbAbreviacion".format(labelValue)
            }
        }
        .build()

    val lineStyle = LineStyle(
        lineType = LineType.SmoothCurve(isDotted = false),
        color = primaryColor,
        width = 4f
    )

    val intersectionPoint = IntersectionPoint(
        color = primaryColor,
        radius = 6.dp,
        alpha = 1.0f
    )

    val selectionHighlightPopUp = SelectionHighlightPopUp(
        backgroundColor = primaryColor,
        backgroundAlpha = 0.9f,
        backgroundCornerRadius = CornerRadius(8f),
        paddingBetweenPopUpAndPoint = 12.dp,
        labelSize = 14.sp,
        labelColor = Color.White,
        popUpLabel = { x, y ->
            val formattedWeight = when (currentUnit) {
                UnitType.GRAMS -> "${y.roundToInt()} $grAbreviacion"
                UnitType.KILOGRAMS -> "%.2f $kgAbreviacion".format(y)
                UnitType.POUNDS -> "%.2f $lbAbreviacion".format(y)
            }
            "Índice: ${x.toInt() + 1}\n Peso: $formattedWeight"
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
        gridLines = GridLines(color = gridColor.copy(alpha = 0.5f)),
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
    val barColor1 = MaterialTheme.colorScheme.primary
    val barColor2 = MaterialTheme.colorScheme.secondary
    val barColors = listOf(barColor1, barColor2)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val desconocido = stringResource(R.string.desconocido)
    val counts = materiales.groupingBy { it.categoria.ifEmpty { desconocido } }.eachCount()

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
        .bottomPadding(8.dp)
        .axisLabelAngle(0f)
        .labelData { index -> (index + 1).toString() }
        .axisLineColor(barColor1)
        .axisLabelColor(onSurfaceColor)
        .startDrawPadding(30.dp)
        .typeFace(customTypceFace ?: Typeface.DEFAULT)
        .build()

    val maxCount = counts.values.maxOrNull() ?: 1
    val yAxisIntervals = if (maxCount < 5) maxCount else 4

    val yAxisData = AxisData.Builder()
        .steps(yAxisIntervals)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(barColor1)
        .axisLabelColor(onSurfaceColor)
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
                        color = onSurfaceColor,
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
                        color = onSurfaceColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PieChartMetales(materiales: List<MaterialItem>) {
    val metalColor = MaterialTheme.colorScheme.secondary
    val nonMetalColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

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
                color = metalColor
            ),
            PieChartData.Slice(
                label = stringResource(R.string.noMetales),
                value = nonMetalCount.toFloat(),
                color = nonMetalColor
            )
        )
    }

    val pieChartData = PieChartData(
        slices = pieSlices,
        plotType = PlotType.Pie
    )
    val pieChartConfig = PieChartConfig(
        showSliceLabels = true,
        sliceLabelTextColor = MaterialTheme.colorScheme.onPrimary,
        sliceLabelTextSize = 14.sp,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        labelColor = onSurfaceColor,
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
    val pieColorRed = RedAlert
    val pieColorGreen = GreenSensor
    val pieColorBlue = NeonBlue
    val pieColorGray = Color.Gray
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val colorCounts = materiales.groupingBy { it.color }.eachCount()
    val totalCount = materiales.size.toFloat()

    var selectedSlice by remember { mutableStateOf<PieChartData.Slice?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val colorMap = mapOf(
        "Rojo" to pieColorRed,
        "Verde" to pieColorGreen,
        "Azul" to pieColorBlue,
        "No hay color" to pieColorGray
    )
    val defaultPieColor = MaterialTheme.colorScheme.tertiary

    val pieSlices = if (totalCount == 0f) {
        emptyList()
    } else {
        colorCounts.map { (colorName, count) ->
            PieChartData.Slice(
                label = colorName.ifBlank { stringResource(R.string.desconocido) },
                value = count.toFloat(),
                color = colorMap[colorName.ifBlank { "No hay color" }] ?: defaultPieColor
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
        labelColor = onSurfaceColor,
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
                color = onSurfaceColor
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Porcentaje: ${"%.1f".format(percentage)}%",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun BarChartDistribucion(weightDistribution: Map<String, Int>) {
    val barColor1 = MaterialTheme.colorScheme.primary
    val barColor2 = MaterialTheme.colorScheme.secondary
    val barColors = listOf(barColor1, barColor2, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val sortedKeys = listOf("1-50g", "51-100g", "101-200g", "201-500g", "501g+")
    val bars = sortedKeys.mapIndexedNotNull { idx, key ->
        val value = weightDistribution[key]
        if (value != null && value > 0) {
            BarData(
                point = Point(idx.toFloat(), value.toFloat()),
                label = key,
                color = barColors[idx % barColors.size]
            )
        } else {
            null
        }
    }

    if (bars.isEmpty()) {
        Text(
            text = stringResource(R.string.noHayDatos),
            modifier = Modifier.padding(32.dp),
            color = onSurfaceColor
        )
        return
    }

    val context = LocalContext.current
    val customTypceFace = remember(context) {
        ResourcesCompat.getFont(context, R.font.roboto_mono_regular)
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(70.dp)
        .bottomPadding(8.dp)
        .axisLabelAngle(0f)
        .labelData { index ->
            bars.getOrNull(index)?.label?.substringBefore("-").plus("g")
        }
        .axisLineColor(barColor1)
        .axisLabelColor(onSurfaceColor)
        .startDrawPadding(30.dp)
        .typeFace(customTypceFace ?: Typeface.DEFAULT)
        .build()

    val maxCount = bars.maxOfOrNull { it.point.y } ?: 1f
    val yAxisIntervals = if (maxCount < 5f) maxCount.toInt() else 4

    val yAxisData = AxisData.Builder()
        .steps(yAxisIntervals)
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(barColor1)
        .axisLabelColor(onSurfaceColor)
        .labelData { value ->
            val scale = maxCount / yAxisIntervals
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
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bars.forEachIndexed { index, barData ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${barData.label}:",
                        color = onSurfaceColor,
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
                        text = "${barData.point.y.toInt()} items",
                        color = onSurfaceColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
import org.jetbrains.skija.*

val chartRenderer = mapOf(
    ChartType.PIE to ::drawPieChart,
    ChartType.BAR to ::drawBarChart,
    ChartType.LINE to ::drawLineChart
)

const val blocksPadding = 5f
const val partForName = 0.07f
const val partForLegend = 0.07f
const val maxPartForChart = 1 - partForName - partForLegend

/**
 * draw [chart] on [renderer] canvas of [width] x [height] size
 */

fun drawChart(renderer: Renderer, width: Int, height: Int, chart: Chart) {
    chartRenderer[chart.type]?.invoke(renderer, width, height, chart.data)
    drawChartName(renderer, width, renderer.chartTop, chart.name)
    if (chart.type == ChartType.PIE)
        drawChartLegend(renderer, width, height - renderer.chartBottom, chart.data.map {it.label})
}

/**
 * draw [name] of chart on [renderer] canvas of [width] x [height] size
 */

fun drawChartName(renderer: Renderer, width: Int, height: Int, name: String) {
    val canvas = renderer.canvas!!
    val textPaint = renderer.textPaint
    val font = renderer.fontAt(height, width, name.length)
    val textWidth = font.measureTextWidth(name, textPaint)

    canvas.drawTextLine(TextLine.make(name, font), (width - textWidth) / 2, height.toFloat(), textPaint)
}


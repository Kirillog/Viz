import org.jetbrains.skija.*

val chartRenderer = mapOf(
    ChartType.BAR to ::drawBarChart,
    ChartType.LINE to ::drawLineChart,
    ChartType.AREA to ::drawAreaChart,
    ChartType.SCATTER to ::drawScatterChart
)

const val blocksPadding = 5f
const val partForName = 0.07f
const val partForLegend = 0.07f
const val maxPartForChart = 1 - partForName - partForLegend

/**
 * draw [chart] on [renderer] canvas of [width] x [height] size
 */

fun drawChart(renderer: Renderer, width: Int, height: Int, chart: Chart) {
    val values = chart.data.map { (it.value * baseToInt).toInt() }
    val labels = chart.data.map { it.label }
    if (chart.type == ChartType.PIE) {
        drawPieChart(renderer, width, height, labels, values)
    } else {
        val field = Field(renderer, width, height, labels, values)
        field.drawCoordinatePlane()
        chartRenderer[chart.type]?.invoke(renderer, field)
        drawChartName(renderer, width, renderer.chartTop, chart.name)
    }

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


import org.jetbrains.skija.*
import kotlin.math.*

val chartRenderer = mapOf(
    ChartType.PIE to ::drawPieChart
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
}
/**
 * draw pie chart of [chartData] on [renderer] canvas of [width] x [height] size
 */

fun drawPieChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val canvas = renderer.canvas!!
    val borderPaint = Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 5f
    }
    val fillPaint = renderer.fillPaint

    // Calculate center and radius of the pie
    val explodeOffset = 10f
    val radius = min(width.toFloat(), height * maxPartForChart) / 2f - 2 * explodeOffset
    val center = Point(width / 2f, height * partForName + height * maxPartForChart / 2f)
    val rect = Rect(
        center.x - radius, center.y - radius,
        center.x + radius, center.y + radius
    )

    /**
     * draw [color] slice of the pie chart, beginning at [startAngle] and rotating on [sweepAngle]
     */
    fun drawSlice(startAngle: Float, sweepAngle: Float, color: Int) {
        // Draw border of Slice
        val path = Path()
        path.moveTo(center)
        path.arcTo(rect, startAngle, sweepAngle, false)
        path.closePath()

        fillPaint.color = color

        // Calculate "explode" transform
        val angle = startAngle + 0.5f * sweepAngle
        val x = explodeOffset * cos(Math.PI * angle / 180).toFloat()
        val y = explodeOffset * sin(Math.PI * angle / 180).toFloat()

        canvas.save()
        canvas.translate(x, y)

        // Fill and stroke the path
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, borderPaint)
        canvas.restore()
    }

    val summaryWeight = chartData.sumOf { (_, value) -> value }
    val coloredChartData = chartData zip colors

    var startAngle = 0f
    coloredChartData.forEach { (data, color) ->
        val (_, value) = data
        val sweepAngle = (value / summaryWeight).toFloat() * 360f
        drawSlice(startAngle, sweepAngle, color)
        startAngle += sweepAngle
    }
    // Calculate chart top and bottom y coordinates with paddings
    renderer.chartTop = (rect.top - explodeOffset - borderPaint.strokeWidth - blocksPadding).toInt()
    renderer.chartBottom = (rect.bottom + explodeOffset + borderPaint.strokeWidth + blocksPadding).toInt()
}
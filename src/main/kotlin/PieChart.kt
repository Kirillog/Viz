import org.jetbrains.skija.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * draw legend of [labels] on [renderer] canvas of [width] x [height] size
 */

fun drawChartLegend(renderer: Renderer, width: Int, height: Int, labels: List<String>) {
    val canvas = renderer.canvas!!
    val textPaint = renderer.textPaint

    val summaryLength = labels.sumOf { it.length + 3 }
    val font = renderer.fontAt(height, width, summaryLength)
    val letterHeight = font.measureText("n").height
    val padding = font.size

    val legendLength = labels.sumOf { font.measureTextWidth(it, textPaint).toDouble() + 3 * padding }.toFloat()
    val circleRadius = font.size / 2
    var leftBorder = width / 2f - legendLength / 2f

    /**
     * draw legend block for [label] with circle of [color], beginning at [left] x coordinate and with [center] y coordinate
     */
    fun drawLegendBlock(label: String, color: Int, left: Float, center: Float) {
        val textRect = font.measureText(label, textPaint)
        val textWidth = textRect.width
        // draw colored circle for label
        var currentLeft = left + padding
        canvas.drawCircle(currentLeft + circleRadius, center, circleRadius, Paint().apply {
            this.color = color
            mode = PaintMode.FILL
        })
        currentLeft += 2 * padding
        canvas.drawTextLine(TextLine.make(label, font), currentLeft, center + letterHeight / 2, textPaint)
        leftBorder = currentLeft + textWidth
    }

    val coloredChartData = labels zip colors
    coloredChartData.forEach { (label, color) ->
        drawLegendBlock(label, color, leftBorder, renderer.chartBottom + circleRadius)
    }
}

/**
 * draw pie chart of [chartData] on [renderer] canvas of [width] x [height] size
 */

fun drawPieChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val canvas = renderer.canvas!!
    val borderPaint = Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 3f
    }
    val fillPaint = renderer.fillPaint

    // Calculate center and radius of the pie
    val explodeOffset = 15f
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

    val values = chartData.map { it.value }
    val summaryWeight = values.sumOf { it }
    val coloredChartData = values zip colors

    var startAngle = 0f
    coloredChartData.forEach { (value, color) ->
        val sweepAngle = (value / summaryWeight).toFloat() * 360f
        drawSlice(startAngle, sweepAngle, color)
        startAngle += sweepAngle
    }
    // Calculate chart top and bottom y coordinates with paddings
    renderer.chartTop = (rect.top - explodeOffset - borderPaint.strokeWidth - blocksPadding).toInt()
    renderer.chartBottom = (rect.bottom + explodeOffset + borderPaint.strokeWidth + blocksPadding).toInt()
}


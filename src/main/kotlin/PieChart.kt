import org.jetbrains.skija.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Draw legend block for [label] with circle of [color], beginning at [left] x coordinate and with [center] y coordinate.
 * Text of legend is specified by [font]
 * @return right x coordinate of border of block
 */
fun drawLegendBlock(renderer: Renderer, label: String, color: Int, left: Float, center: Float, font: Font): Float {
    val canvas = renderer.canvas!!
    val textPaint = renderer.textPaint

    val letterHeight = font.measureText("n").height
    val padding = font.size
    val circleRadius = font.size / 2
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
    return currentLeft + textWidth
}


/**
 * Draw legend of [labels] on [renderer] canvas of [width] x [height] size
 */

fun drawPieChartLegend(renderer: Renderer, width: Int, height: Int, labels: List<String>) {
    val summaryLength = labels.sumOf { it.length + 3 }
    val font = renderer.fontAt(height, width, summaryLength)
    val padding = font.size
    val circleRadius = font.size / 2
    val legendLength = labels.sumOf { font.measureTextWidth(it, renderer.textPaint).toDouble() + 3 * padding }.toFloat()

    var leftBorder = width / 2f - legendLength / 2f
    val coloredChartData = labels zip colors
    coloredChartData.forEach { (label, color) ->
        leftBorder = drawLegendBlock(renderer, label, color, leftBorder, renderer.chartBottom + circleRadius, font)
    }
}

const val explodeOffset = 15f

/**
 * Draw [color] slice of the circle([center], [radius]], beginning at [startAngle] and rotating on [sweepAngle].
 * The border draws by [borderPaint]
 */
fun drawSlice(
    renderer: Renderer,
    startAngle: Float,
    sweepAngle: Float,
    center: Point,
    radius: Float,
    color: Int,
    borderPaint: Paint
) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    // draw border of slice
    val path = Path()
    path.moveTo(center)
    path.arcTo(
        Rect(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius
        ), startAngle, sweepAngle, false
    )
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

/**
 * draw pie chart of [chartData] on [renderer] canvas of [width] x [height] size
 */

fun drawPieChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val borderPaint = Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 3f
    }
    // Calculate center and radius of the pie
    val radius = min(width.toFloat(), height * maxPartForChart) / 2f - 2 * explodeOffset
    val center = Point(width / 2f, height * partForName + height * maxPartForChart / 2f)
    val borderRect = Rect(
        center.x - radius, center.y - radius,
        center.x + radius, center.y + radius
    )

    val values = chartData.map { it.value }
    val summaryWeight = values.sumOf { it }
    val coloredChartData = values zip colors

    var startAngle = 0f
    coloredChartData.forEach { (value, color) ->
        val sweepAngle = (value / summaryWeight).toFloat() * 360f
        drawSlice(renderer, startAngle, sweepAngle, center, radius, color, borderPaint)
        startAngle += sweepAngle
    }
    // Calculate chart top and bottom y coordinates with paddings
    renderer.chartTop = (borderRect.top - explodeOffset - borderPaint.strokeWidth - blocksPadding).toInt()
    renderer.chartBottom = (borderRect.bottom + explodeOffset + borderPaint.strokeWidth + blocksPadding).toInt()
}


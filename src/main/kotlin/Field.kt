import org.jetbrains.skija.*

const val cellHeight = 50f
const val markLength = cellHeight / 3
const val stepY = cellHeight / 5
const val axisPart = 0.1f
const val planePart = (1 - 2 * axisPart)
const val baseToInt = 100

/**
 * calculates scale for [values] of chart data that it can be drawn at [size] segment
 */

fun calculateScaleFor(values: List<Int>, size: Int): Scale {
    fun signsAfterDot(value : Float) : Int {
        return value.toString().dropWhile { it != '.' }.length
    }

    val maxValue = values.maxOf { it }
    var result = maxValue
    var scaleInterval = 10
    val maxNumberOfCells = (size * maxPartForChart * planePart).toInt() / cellHeight
    while (result > maxNumberOfCells) {
        result = (maxValue + scaleInterval - 1) / scaleInterval
        scaleInterval *= 10
    }
    scaleInterval /= 10
    val scaleExtensions = listOf(5, 4, 2)
    scaleExtensions.forEach { scale ->
        if (result * scale < maxNumberOfCells) {
            result *= scale
            return Scale(
                result,
                scaleInterval.toFloat() / (baseToInt * scale),
                signsAfterDot(scaleInterval.toFloat() / (baseToInt * scale))
            )
        }
    }
    return Scale(result, scaleInterval.toFloat() / baseToInt, 0)
}

data class Scale(val marksAmount: Int, val interval: Float, val signs: Int)

/**
 * draw background coordinate plane for charts using [renderer] on [height] x [width] canvas
 * @param scaleX scale of x-axis
 * @param scaleY scale if y-axis
 */

class Field(
    private val renderer: Renderer,
    private val height: Int, private val width: Int,
    scaleX: Scale, private val scaleY: Scale,
) {
    val cellWidth = width * planePart / scaleX.marksAmount
    private val xMarks = List(scaleX.marksAmount + 1) { x -> width * axisPart + x * cellWidth }
    val movedXMarks = xMarks.map { it + cellWidth / 2 }
    val yMarks = List(scaleY.marksAmount + 1) { y -> height * partForName + cellHeight * y }.reversed()
    val valueMarks = List(yMarks.size) { it * scaleY.interval * baseToInt }

    private val canvas = renderer.canvas!!
    private val borderPaint = renderer.borderPaint
    private val textPaint = renderer.textPaint

    private fun drawNumberYAxis(startX: Float) {
        val marks = List(yMarks.size) { it * scaleY.interval }
        val maxStringLength = designNumber(marks.last()).length
        val axisFont = renderer.fontAt((cellHeight / 2).toInt(), (width * axisPart - markLength).toInt(), maxStringLength)
        val yValues = marks zip yMarks
        for ((mark, y) in yValues) {
            canvas.drawLine(startX, y, startX - markLength, y, borderPaint)
            val number = designNumber(mark)
            val rectText = axisFont.measureText(number, renderer.textPaint)
            val textHeight = rectText.height / 2
            val textWidth = rectText.width
            canvas.drawTextLine(
                TextLine.make(number, axisFont),
                startX - textWidth - cellHeight / 2,
                y + textHeight,
                textPaint
            )
        }
    }

    private fun drawCategoryXAxis(
        startY: Float,
        labels: List<String>,
    ) {
        val maxStringLength = labels.sumOf { label -> label.length + 1 }
        val axisFont = renderer.fontAt((height * axisPart).toInt(), cellWidth.toInt(), maxStringLength)
        val xLabels = labels zip movedXMarks
        xLabels.forEach { (label, x) ->
            canvas.drawLine(x, startY, x, startY + markLength, borderPaint)
            val rectText = axisFont.measureText(label, renderer.textPaint)
            val textHeight = rectText.height
            val textWidth = rectText.width / 2
            canvas.drawTextLine(
                TextLine.make(label, axisFont),
                x - textWidth,
                startY + textHeight + cellHeight / 2,
                renderer.textPaint
            )
        }
    }

    private fun designNumber(value: Float): String {
        val result = StringBuilder()
        val signsAfterDot = if (scaleY.signs > 0) {
            value.toString().dropWhile { it != '.' }.padEnd(scaleY.signs, '0')
        } else
            ""
        var number = value.toInt()
        var bit = 0
        do {
            result.append(number % 10)
            if (bit % 3 == 2 && number > 10)
                result.append(',')
            number /= 10
            bit++
        } while (number != 0)
        return result.reverse().toString() + signsAfterDot
    }

    private fun drawCheckeredPlane() {
        val coordinatePaint = Paint().apply {
            mode = PaintMode.STROKE
            pathEffect = PathEffect.makeDash(FloatArray(2) { 3f }, 0f)
            strokeWidth = borderPaint.strokeWidth / 2
        }
        val borderRect = Rect(xMarks.first(), yMarks.last(), xMarks.last(), yMarks.first())

        canvas.drawRect(borderRect, borderPaint)
        for (x in xMarks.dropLast(1).drop(1))
            canvas.drawLine(x, yMarks.first(), x, yMarks.last(), coordinatePaint)
        for (y in yMarks.dropLast(1).drop(1))
            canvas.drawLine(xMarks.first(), y, xMarks.last(), y, coordinatePaint)

        // Calculate chart top and bottom y coordinates with paddings
        renderer.chartTop = (borderRect.top - borderPaint.strokeWidth - blocksPadding).toInt()
        renderer.chartBottom = (borderRect.bottom + borderPaint.strokeWidth + blocksPadding).toInt()
    }

    fun drawCoordinatePlane(labels: List<String>) {
        drawCheckeredPlane()
        drawNumberYAxis(xMarks.first())
        drawCategoryXAxis(yMarks.first(), labels)
    }
}
import org.jetbrains.skija.*

const val cellHeight = 50f
const val markLength = cellHeight / 3
const val stepY = cellHeight / 5
const val axisPart = 0.1f
const val planePart = (1 - 2 * axisPart)
const val baseToInt = 100

/**
 * draw background coordinate plane for charts of [labels] and [values] using [renderer] on [height] x [width] canvas
 */

class Field(
    private val renderer: Renderer,
    private val width: Int, private val height: Int,
    private val labels: List<String>, private val values: List<Int>
) {
    val scaleX = Scale(labels.size, 1f, 0)
    val scaleY = calculateScaleFor(values, height)
    val cellWidth = width * planePart / scaleX.marksAmount

    var yMarks = listOf<Float>()
    var xMarks = listOf<Float>()
    var dataX = listOf<Float>()
    var dataY = listOf<Float>()
    var valueMarks = listOf<Float>()

    init {
        xMarks = List(scaleX.marksAmount + 1) { x -> width * axisPart + x * cellWidth }
        yMarks = List(scaleY.marksAmount + 1) { y -> height * partForName + cellHeight * y }.reversed()
        valueMarks = List(yMarks.size) { it * scaleY.interval * baseToInt }
        dataX = xMarks.map { it + cellWidth / 2 }.dropLast(1)
        dataY = calculateYByValue()
    }

    private val canvas = renderer.canvas!!
    private val borderPaint = renderer.borderPaint
    private val textPaint = renderer.textPaint

    /**
     * calculates y coordinate for each value of [values] depending on [scaleX] and [scaleY]
     */

    private fun calculateYByValue(): List<Float> {
        val result = mutableListOf<Float>()
        for (i in 0 until scaleX.marksAmount) {
            for (j in 0 until scaleY.marksAmount) {
                if (valueMarks[j] <= values[i] && values[i] <= valueMarks[j + 1]) {
                    val coefficient = (values[i] - valueMarks[j]) / (scaleY.interval * baseToInt)
                    result.add(yMarks[j] - coefficient * cellHeight)
                    break
                }
            }
        }
        return result
    }

    private fun designNumber(value: Int): String {
        val result = StringBuilder()
        // add trailing zeros for float scale
        val signsAfterDot = if (scaleY.signs > 0) {
            (value / baseToInt.toFloat()).toString().dropWhile { it != '.' }.padEnd(scaleY.signs, '0')
        } else
            ""
        var number = value / baseToInt
        var bit = 0
        do {
            result.append(number % 10)
            // add separators for each 3 sign
            if (bit % 3 == 2 && number > 10)
                result.append(',')
            number /= 10
            bit++
        } while (number != 0)
        return result.reverse().toString() + signsAfterDot
    }

    private fun drawNumberYAxis(startX: Float) {
        val marks = List(yMarks.size) { (it * scaleY.interval * baseToInt).toInt() }
        val maxStringLength = designNumber(marks.last()).length
        val axisFont =
            renderer.fontAt((cellHeight / 2).toInt(), (width * axisPart - markLength).toInt(), maxStringLength)
        val yValues = marks zip yMarks
        for ((mark, y) in yValues) {
            // draw mark on axis
            canvas.drawLine(startX, y, startX - markLength, y, borderPaint)
            val number = designNumber(mark)
            val rectText = axisFont.measureText(number, renderer.textPaint)
            val textHeight = rectText.height / 2
            val textWidth = rectText.width
            // draw number for mark on axis
            canvas.drawTextLine(
                TextLine.make(number, axisFont),
                startX - textWidth - cellHeight / 2,
                y + textHeight,
                textPaint
            )
        }
        // draw mini marks between main marks on axis
        val miniMarks =
            List(scaleY.marksAmount * (cellHeight / stepY).toInt()) { y -> height * partForName + stepY * y }
        for (i in miniMarks.indices)
            if (i % 5 != 0)
                canvas.drawLine(startX, miniMarks[i], startX - stepY, miniMarks[i], borderPaint)
    }

    private fun drawCategoryXAxis(
        startY: Float,
        labels: List<String>,
    ) {
        val maxStringLength = labels.maxOf { label -> label.length }
        val axisFont = renderer.fontAt((height * axisPart).toInt(), cellWidth.toInt(), maxStringLength)
        val xLabels = labels zip dataX
        xLabels.forEach { (label, x) ->
            // draw marks on axis
            canvas.drawLine(x, startY, x, startY + markLength, borderPaint)
            val rectText = axisFont.measureText(label, renderer.textPaint)
            val textHeight = axisFont.size
            val textWidth = rectText.width / 2
            // draw labels for mark
            canvas.drawTextLine(
                TextLine.make(label, axisFont),
                x - textWidth,
                startY + textHeight + cellHeight / 2,
                renderer.textPaint
            )
        }
    }

    private fun drawCheckeredPlane() {
        val coordinatePaint = Paint().apply {
            mode = PaintMode.STROKE
            pathEffect = PathEffect.makeDash(FloatArray(2) { 3f }, 0f)
            strokeWidth = borderPaint.strokeWidth / 2
        }
        val borderRect = Rect(xMarks.first(), yMarks.last(), xMarks.last(), yMarks.first())

        // draw checkered field
        canvas.drawRect(borderRect, borderPaint)
        for (x in xMarks.dropLast(1).drop(1))
            canvas.drawLine(x, yMarks.first(), x, yMarks.last(), coordinatePaint)
        for (y in yMarks.dropLast(1).drop(1))
            canvas.drawLine(xMarks.first(), y, xMarks.last(), y, coordinatePaint)

        // calculate chart top and bottom y coordinates with paddings
        renderer.chartTop = (borderRect.top - borderPaint.strokeWidth - blocksPadding).toInt()
        renderer.chartBottom = (borderRect.bottom + borderPaint.strokeWidth + blocksPadding).toInt()
    }

    fun drawCoordinatePlane() {
        drawCheckeredPlane()
        drawNumberYAxis(xMarks.first())
        drawCategoryXAxis(yMarks.first(), labels)
    }
}



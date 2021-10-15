import org.jetbrains.skija.Color
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode

/**
 * calculates y coordinate for each value of [values] in [field]
 */

fun calculateYByValue(field: Field, values: List<Int>): List<Float> {
    val result = mutableListOf<Float>()
    for (i in 0 until field.scaleX.marksAmount) {
        for (j in 0 until field.scaleY.marksAmount) {
            if (field.valueMarks[j] <= values[i] && values[i] <= field.valueMarks[j + 1]) {
                val coefficient = (values[i] - field.valueMarks[j]) / (field.scaleY.interval * baseToInt)
                result.add(field.yMarks[j] - coefficient * cellHeight)
                break
            }
        }
    }
    return result
}

/**
 * draw line chart of [chartData] on [renderer] canvas of [width] x [height] size
 */

fun drawLineChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val canvas = renderer.canvas!!
    val fillPaint = Paint().apply {
        color = colors.first()
        mode = PaintMode.FILL
        strokeWidth = 3f
    }
    val whiteFillPaint = Paint().apply {
        color = Color.makeRGB(255, 255, 255)
        mode = PaintMode.FILL
        strokeWidth = 3f
    }

    val values = chartData.map { (it.value * baseToInt).toInt() }
    val labels = chartData.map { it.label }

    val xScale = Scale(chartData.size, 1f, 0)
    val yScale = calculateScaleFor(values, height)

    val field = Field(renderer, height, width, xScale, yScale)
    field.drawCoordinatePlane(labels)

    val coordinateY = calculateYByValue(field, values)

    // draw segments for neighbouring points
    for (i in 0 until coordinateY.size - 1)
        canvas.drawLine(field.movedXMarks[i], coordinateY[i], field.movedXMarks[i + 1], coordinateY[i + 1], fillPaint)
    // draw circles for each point
    for (i in coordinateY.indices) {
        canvas.drawCircle(field.movedXMarks[i], coordinateY[i], 5f, fillPaint)
        canvas.drawCircle(field.movedXMarks[i], coordinateY[i], 3f, whiteFillPaint)
    }
}
import org.jetbrains.skija.*

/**
 * draw bar chart of [chartData] on [renderer] canvas of [width] x [height] size
 */

fun drawBarChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    fillPaint.color = colors.first()

    val values = chartData.map { (it.value * baseToInt).toInt() }
    val labels = chartData.map { it.label }

    val xScale = Scale(chartData.size, 1f, 0)
    val yScale = calculateScaleFor(values, height)

    val field = Field(renderer, height, width, xScale, yScale)
    field.drawCoordinatePlane(labels)

    for (i in values.indices) {
        for (j in 0 until field.yMarks.size - 1) {
            if (field.valueMarks[j] <= values[i] && values[i] <= field.valueMarks[j + 1]) {
                val coefficient = (values[i] - field.valueMarks[j]) / (yScale.interval * baseToInt)
                val rect = Rect(
                    field.movedXMarks[i] - field.cellWidth / 4,
                    field.yMarks[j] - coefficient * cellHeight,
                    field.movedXMarks[i] + field.cellWidth / 4,
                    field.yMarks.first()
                )
                canvas.drawRect(
                    rect,
                    fillPaint
                )
            }
        }
    }

}
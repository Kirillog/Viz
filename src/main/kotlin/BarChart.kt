import org.jetbrains.skija.Rect

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
    val coordinateY = calculateYByValue(field, values)

    for (i in 0 until field.scaleX.marksAmount) {
        canvas.drawRect(
            Rect(
                field.movedXMarks[i] - field.cellWidth / 4,
                coordinateY[i],
                field.movedXMarks[i] + field.cellWidth / 4,
                field.yMarks.first()
            ),
            fillPaint
        )
    }
}
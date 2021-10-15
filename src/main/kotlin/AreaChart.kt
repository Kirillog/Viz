import org.jetbrains.skija.*
import org.jetbrains.skija.Color

fun drawAreaChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
    val canvas = renderer.canvas!!
    val fillPaint = Paint().apply {
        color = colors.first()
        mode = PaintMode.FILL
        strokeWidth = 3f
    }
    val fillTransparent = Paint().apply {
        color = fillPaint.color
        alpha = 100
        mode = PaintMode.FILL
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

    // form array of points of chart
    val points = Array(coordinateY.size + 2) { Point(0f, 0f) }
    for (i in coordinateY.indices)
        points[i] = Point(field.movedXMarks[i], coordinateY[i])
    points[coordinateY.size] = Point(field.movedXMarks.last(), field.yMarks.first())
    points[coordinateY.size + 1] = Point(field.movedXMarks.first(), field.yMarks.first())

    val path = Path()
    path.addPoly(points, true)
    // fill area
    canvas.drawPath(path, fillTransparent)
    // draw segments for neighbouring points
    for (i in 0 until coordinateY.size - 1)
        canvas.drawLine(field.movedXMarks[i], coordinateY[i], field.movedXMarks[i + 1], coordinateY[i + 1], fillPaint)
    // draw circles for each point
    for (i in points.dropLast(2)) {
        canvas.drawCircle(i.x, i.y, 5f, fillPaint)
        canvas.drawCircle(i.x, i.y, 3f, whiteFillPaint)
    }
}
import org.jetbrains.skija.*

/**
 * draw area chart of at [field] on [renderer] canvas
 */
fun drawAreaChart(renderer: Renderer, field : Field) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    val fillTransparent = Paint().apply {
        color = fillPaint.color
        alpha = 100
        mode = PaintMode.FILL
    }
    val whiteFillPaint = renderer.whiteFillPaint

    // form array of points of chart
    val points = Array(field.dataY.size + 2) { Point(0f, 0f) }
    for (i in field.dataY.indices)
        points[i] = Point(field.dataX[i], field.dataY[i])
    points[field.dataY.size] = Point(field.dataX.last(), field.yMarks.first())
    points[field.dataY.size + 1] = Point(field.dataX.first(), field.yMarks.first())

    val path = Path()
    path.addPoly(points, true)
    // fill area
    canvas.drawPath(path, fillTransparent)
    // draw segments for neighbouring points
    for (i in 0 until field.dataY.size - 1)
        canvas.drawLine(field.dataX[i], field.dataY[i], field.dataX[i + 1], field.dataY[i + 1], fillPaint)
    // draw circles for each point
    for (i in points.dropLast(2)) {
        canvas.drawCircle(i.x, i.y, 5f, fillPaint)
        canvas.drawCircle(i.x, i.y, 3f, whiteFillPaint)
    }
}
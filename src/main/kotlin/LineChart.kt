/**
 * draw line chart at [field] on [renderer] canvas
 */

fun drawLineChart(renderer: Renderer, field : Field) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    val whiteFillPaint = renderer.whiteFillPaint

    // draw segments for neighbouring points
    for (i in 0 until field.dataY.size - 1)
        canvas.drawLine(field.dataX[i], field.dataY[i], field.dataX[i + 1], field.dataY[i + 1], fillPaint)
    // draw circles for each point
    for (i in field.dataY.indices) {
        canvas.drawCircle(field.dataX[i], field.dataY[i], 5f, fillPaint)
        canvas.drawCircle(field.dataX[i], field.dataY[i], 3f, whiteFillPaint)
    }
}
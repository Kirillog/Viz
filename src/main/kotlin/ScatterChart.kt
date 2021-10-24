/**
 * draw scatter chart at [field] on [renderer] canvas
 */

fun drawScatterChart(renderer: Renderer, field: Field) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    val whiteFillPaint = renderer.whiteFillPaint

    // draw circles for each point
    for (i in 0 until field.scaleX.marksAmount) {
        canvas.drawCircle(field.dataX[i], field.dataY[i], 5f, fillPaint)
        canvas.drawCircle(field.dataX[i], field.dataY[i], 3f, whiteFillPaint)
    }
}
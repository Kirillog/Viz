import org.jetbrains.skija.Rect

/**
 * draw bar chart at [field] on [renderer] canvas
 */

fun drawBarChart(renderer: Renderer, field: Field) {
    val canvas = renderer.canvas!!
    val fillPaint = renderer.fillPaint
    fillPaint.color = colors.first()

    // draw bars for values
    for (i in 0 until field.scaleX.marksAmount) {
        canvas.drawRect(
            Rect(
                field.dataX[i] - field.cellWidth / 4,
                field.dataY[i],
                field.dataX[i] + field.cellWidth / 4,
                field.yMarks.first()
            ),
            fillPaint
        )
    }
}
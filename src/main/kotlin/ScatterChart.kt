import org.jetbrains.skija.Color
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode

fun drawScatterChart(renderer: Renderer, width: Int, height: Int, chartData: List<ChartData>) {
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

    for (i in 0 until field.scaleX.marksAmount) {
        canvas.drawCircle(field.movedXMarks[i], coordinateY[i], 5f, fillPaint)
        canvas.drawCircle(field.movedXMarks[i], coordinateY[i], 3f, whiteFillPaint)
    }
}
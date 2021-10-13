import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import javax.swing.WindowConstants
import kotlin.math.min

fun createWindowOf(chart: Chart) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = chart.name

    window.layer.renderer = Renderer(window.layer) {
        renderer, width, height -> drawChart(renderer, width, height, chart)
    }

    window.preferredSize = Dimension(800, 600)
    window.minimumSize = Dimension(100, 100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

class Renderer(
    val layer: SkiaLayer,
    val drawChart: (Renderer, Int, Int) -> Unit
) : SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    var chartTop = 0
    var chartBottom = 0
    var canvas: Canvas? = null
    val font = Font(typeface, 40f)

    val textPaint = Paint().apply {
        mode = PaintMode.FILL
        strokeWidth = 3f
    }

    val paint = Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 1f
    }

    val fillPaint = Paint().apply {
        mode = PaintMode.FILL
    }

    fun calculateFont(height : Int, width: Int, textLength:Int): Font {
        return Font(typeface, min(font.size, min(height.toFloat(), width.toFloat() / textLength)))
    }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        this.canvas = canvas
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        drawChart(this, (width / contentScale).toInt(), (height / contentScale).toInt())
        layer.needRedraw()
    }
}
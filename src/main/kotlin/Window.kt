import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.toBufferedImage
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileFilter
import kotlin.math.min

class ChartFileFilter(var extension: String?, private var description: String?) : FileFilter() {

    override fun accept(file: File?): Boolean {
        if (file != null) {
            return if (file.isDirectory)
                true
            else if (extension == null)
                extension!!.isEmpty()
            else
                file.name.endsWith(extension!!)
        }
        return false
    }

    override fun getDescription(): String? {
        return description
    }
}

@DelicateCoroutinesApi
fun createMenu(window: JFrame, panel: SkiaPanel) {
    val menuBar = JMenuBar()
    val fileMenu = JMenu("Chart")
    menuBar.add(fileMenu)

    val fileChooser = JFileChooser()

    val miTakeScreenshot = JMenuItem("Screenshot")
    val ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
    miTakeScreenshot.accelerator = ctrlS
    miTakeScreenshot.addActionListener {
        fileChooser.dialogType = JFileChooser.SAVE_DIALOG
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        val fileFilter = ChartFileFilter("png", "Charts (*.png)")
        fileChooser.addChoosableFileFilter(fileFilter)
        val result = fileChooser.showSaveDialog(panel)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = if (fileFilter.accept(fileChooser.selectedFile))
                fileChooser.selectedFile
            else
                File("${fileChooser.selectedFile.absolutePath}.${fileFilter.extension}")
            val screenshot = panel.layer.screenshot()!!
            GlobalScope.launch(Dispatchers.IO) {
                val image = screenshot.toBufferedImage()
                ImageIO.write(image, "png", selectedFile)
                println("Saved to ${selectedFile.absolutePath}")
            }
        }
    }
    fileMenu.add(miTakeScreenshot)
    window.jMenuBar = menuBar
}

@DelicateCoroutinesApi
fun createWindowOf(chart: Chart) = SwingUtilities.invokeLater {
    val window = JFrame()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = chart.name

    val panel = SkiaPanel()
    panel.layer.renderer = Renderer(panel.layer) { renderer, width, height ->
        drawChart(renderer, width, height, chart)
    }
    window.contentPane.add(panel)

    createMenu(window, panel)

    window.pack()
    window.isVisible = true
    window.size = Dimension(800, 600)
    window.minimumSize = Dimension(200, 200)
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

    val borderPaint = Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 0.4f
    }

    val fillPaint = Paint().apply {
        mode = PaintMode.FILL
    }

    fun fontAt(height: Int, width: Int, textLength: Int): Font {
        return Font(typeface, calculateFontSize(height, width, textLength))
    }

    private fun calculateFontSize(height: Int, width: Int, textLength: Int): Float {
        return min(font.size, min(height.toFloat(), width.toFloat() / textLength))
    }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        this.canvas = canvas
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        drawChart(this, (width / contentScale).toInt(), (height / contentScale).toInt())
        layer.needRedraw()
    }
}
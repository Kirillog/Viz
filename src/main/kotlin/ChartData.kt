import org.jetbrains.skija.Color
import kotlin.random.Random

const val maxDataSize = 15

data class ChartData(val label: String, val value: Double)

data class Chart(val name: String, val type: ChartType, val data: List<ChartData>)

enum class ChartType {
    PIE, BAR, LINE, AREA, SCATTER;

    override fun toString(): String {
        return this.name.lowercase()
    }
}

val colors = List(maxDataSize) { Color.makeRGB(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) }


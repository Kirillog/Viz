import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScaleTest {
    @Test
    fun integerScaling() {
        val scale = calculateScaleFor(
            listOf(
                1024500,
                2396300,
                1503800
            ),
            538
        )
        assertEquals(5000f, scale.interval)
        assertEquals(6, scale.marksAmount)
        assertEquals(0, scale.signs)
    }

    @Test
    fun floatScaling() {
        val scale = calculateScaleFor(
            listOf(
                250,
                220,
                230,
                190,
                180
            ),
            538
        )
        assertEquals(0.5f, scale.interval)
        assertEquals(6, scale.marksAmount)
        assertEquals(2, scale.signs)
    }
}
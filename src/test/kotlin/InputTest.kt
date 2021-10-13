import org.junit.jupiter.api.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InputTest {
    private val standardErr = System.err
    private val stream = ByteArrayOutputStream()

    @BeforeAll
    fun setUp() {
        System.setErr(PrintStream(stream))
    }

    @AfterAll
    fun tearDown() {
        System.setErr(standardErr)
    }

    @BeforeEach
    fun reset() {
        stream.reset()
    }

    @Test
    fun extraArgumentsTest() {
        parse(arrayOf("nameOfFile1.txt", "nameOfFile2.txt"))
        assertEquals("Too many arguments, so reading from standard input", stream.toString().trim())
    }

    @Test
    fun cannotReadFileTest() {
        parse(arrayOf("nameOfFile1.txt"))
        assertEquals("Cannot open 'nameOfFile1.txt' file, so reading from standard input", stream.toString().trim())
    }
}

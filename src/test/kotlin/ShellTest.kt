import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.ValueSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShellTest {
    private val shell = Shell()
    private val standardErr = System.err
    private val standardIn = System.`in`
    private val standardOut = System.out
    private val streamErr = ByteArrayOutputStream()
    private val streamOut = ByteArrayOutputStream()

    private fun writeInput(data: String) {
        System.setIn(ByteArrayInputStream(data.toByteArray()))
    }

    @BeforeAll
    fun setUp() {
        System.setOut(PrintStream(streamOut))
        System.setErr(PrintStream(streamErr))
    }

    @AfterAll
    fun tearDown() {
        System.setIn(standardIn)
        System.setOut(standardOut)
        System.setErr(standardErr)
    }

    @Nested
    inner class ReadChartType {
        @BeforeEach
        fun reset() {
            streamErr.reset()
        }

        @Test
        fun invalidCommandType() {
            writeInput("redraw")
            val exception = assertThrows<IOException> { shell.readChartType() }
            assertEquals("Unknown command -- 'redraw'", exception.message)
        }

        @Test
        fun quitCommand() {
            writeInput("quit")
            assertEquals(null, shell.readChartType())
        }

        @Test
        fun endOfInput() {
            assertEquals(null, shell.readChartType())
        }

        @Test
        fun invalidChartType() {
            writeInput("draw scater")
            val exception = assertThrows<IOException> { shell.readChartType() }
            assertEquals("Unknown type of chart -- 'scater'", exception.message)
        }

        @Test
        fun missingOperandForDraw() {
            writeInput("draw")
            val exception = assertThrows<IOException> { shell.readChartType() }
            assertEquals("Missing operand after draw", exception.message)
        }

        @Test
        fun extraOperandForDraw() {
            writeInput("draw scatter plot")
            val exception = assertThrows<IOException> { shell.readChartType() }
            assertEquals("Extra operands after draw", exception.message)
        }

        @ParameterizedTest
        @ValueSource(strings = ["pie", "bar", "line", "area", "scatter"])
        fun readChartType(type: String) {
            writeInput("draw $type")
            assertEquals(ChartType.valueOf(type.uppercase()), shell.readChartType())
        }

        @ParameterizedTest
        @ValueSource(strings = ["pie", "bar", "line", "area", "scatter"])
        fun readChartTypeWithNewLines(type: String) {
            writeInput("\n\n\ndraw $type")
            assertEquals(ChartType.valueOf(type.uppercase()), shell.readChartType())
        }
    }

    @Nested
    inner class ReadChartName {
        @Test
        fun readChartNameWithNewLines() {
            writeInput("\n\n\nName")
            assertEquals("Name", shell.readChartName())
        }

        @Test
        fun endOfInput() {
            assertEquals(null, shell.readChartName())
        }
    }


    @Nested
    inner class ReadChartData {
        @BeforeEach
        fun reset() {
            streamErr.reset()
        }

        @Test
        fun endOfInput() {
            assertEquals(emptyList(), shell.readChartData())
        }

        @Test
        fun emptyData() {
            writeInput("")
            assertEquals(emptyList(), shell.readChartData())
        }

        @Test
        fun invalidDataSeparator() {
            writeInput(
                """
                Data->1
                Something:goes_wrong
                """.trimIndent()
            )
            assertEquals(listOf(ChartData("Data", 1.0)), shell.readChartData())
            assertEquals(
                "${Color.RED}Incorrect data, expected: label->number${Color.RESET}",
                streamErr.toString().trim()
            )
        }

        @Test
        fun incorrectDataType() {
            writeInput(
                """
                Something->integer
                Data->1
                """.trimIndent()
            )
            assertEquals(listOf(ChartData("Data", 1.0)), shell.readChartData())
            assertEquals(
                "${Color.RED}Incorrect data, expected: label->number${Color.RESET}",
                streamErr.toString().trim()
            )
        }

        @Test
        fun readChartData() {
            writeInput(
                """
                Windows->78
                OS X->10
                Other->3
                Linux->2
                Chrome OS->1
            """.trimIndent()
            )
            assertEquals(
                listOf(
                    ChartData("Windows", 78.0),
                    ChartData("OS X", 10.0),
                    ChartData("Other", 3.0),
                    ChartData("Linux", 2.0),
                    ChartData("Chrome OS", 1.0)
                ),
                shell.readChartData()
            )
            assertEquals("", streamErr.toString())
        }
    }
}
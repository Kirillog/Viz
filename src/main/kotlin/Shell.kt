import java.io.File
import java.io.IOException

class Shell(file: File?) {
    var exit = false
    var readFromFile = false
    private val enableChartTypes = mutableSetOf<String>()

    init {
        if (file != null) {
            readFromFile = true
            Color.values().forEach { color ->
                color.code = ""
            }
            System.setIn(file.inputStream())
        } else {
            System.setErr(System.out)
        }
        ChartType.values().forEach { type ->
            enableChartTypes.add(type.toString().lowercase())
        }
    }

    /**
     * reads command from standard input and returns it
     */

    fun readChartType(): ChartType? {
        var line: String?
        do {
            printMessage("command")
            line = readLine()?.trim()
        } while (line == "")
        var arguments = line?.split(" ") ?: return null
        val stringOperation = arguments[0]
        arguments = arguments.drop(1)
        // check if operation is available and has correct number of arguments
        return when (stringOperation) {
            "draw" -> {
                if (arguments.isEmpty())
                    throw IOException("Incorrect number of arguments for $stringOperation")
                if (!enableChartTypes.contains(arguments.first()))
                    throw IOException("Unknown type of chart -- '${arguments.first()}'")
                ChartType.valueOf(arguments.first().uppercase())
            }
            "quit" ->
                null
            else ->
                throw IOException("Unknown command -- '$stringOperation'")
        }
    }

    /**
     * read chart name from standard input and returns it
     */

    fun readChartName(): String? {
        var line: String?
        do {
            printMessage("name")
            line = readLine()?.trim()
        } while (line == "")
        return line
    }

    /**
     * read chart dates from standard input and returns it
     */

    fun readChartData(): List<ChartData> {
        val chartData = mutableListOf<ChartData>()
        printMessage("data")
        var line = readLine()?.trim()
        while (!line.isNullOrEmpty()) {
            val arguments = line.split("->")
            if (arguments.size != 2 || arguments[1].toDoubleOrNull() == null)
                printError("Incorrect data, expected: label->number")
            else
                chartData.add(ChartData(arguments[0], arguments[1].toDouble()))
            printMessage("data")
            line = readLine()?.trim()
        }
        return chartData
    }

    /**
     * draw [chart] at new window
     */

    fun drawChart(chart: Chart) {
        createWindowOf(chart)
    }

    private fun printMessage(msg: String) {
        if (!readFromFile)
            print("${Color.BLUE}$msg: ${Color.RESET}")
    }

    fun println(message: String) {
        kotlin.io.println("${Color.GREEN}$message${Color.RESET}")
    }

    fun printError(message: String?) {
        if (message != null)
            System.err.println("${Color.RED}$message${Color.RESET}")
    }

}

enum class Color(var code: String) {
    RESET("\u001B[0m"), RED("\u001B[31m"), GREEN("\u001B[32m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m");

    override fun toString(): String {
        return this.code
    }
}

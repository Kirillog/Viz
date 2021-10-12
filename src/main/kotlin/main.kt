import java.io.IOException

fun main() {
    val shell = Shell()
    while (!shell.exit) {
        try {
            val chartType = shell.readChartType() ?: break
            val chartName = shell.readChartName() ?: break
            val chartData = shell.readChartData()
            shell.drawChart(Chart(chartName, chartType, chartData))
        } catch (err: IOException) {
            shell.printError(err.message)
        }
    }
}
fun main(args:Array<String>) {
    val file = parse(args)
    val shell = Shell(file)
    while (!shell.exit) {
        try {
            val chartType = shell.readChartType() ?: break
            val chartName = shell.readChartName() ?: break
            val chartData = shell.readChartData()
            shell.drawChart(Chart(chartName, chartType, chartData))
        } catch (err: Exception) {
            shell.printError(err.message)
        }
    }
}
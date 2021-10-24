/**
 * Calculates scale for [values] that will be marked on free space of [length]
 */

fun calculateScaleFor(values: List<Int>, length: Int): Scale {
    fun signsAfterDot(value: Float): Int {
        val mantissa = value.toString().dropWhile { it != '.' }
        return if (mantissa == ".0")
            0
        else
            mantissa.length
    }

    val maxValue = values.maxOf { it }
    var result = maxValue
    var scaleInterval = 10
    val maxNumberOfCells = (length * (1 - partForName) * (1 - axisPart)).toInt() / cellHeight
    // calculate minimum possible scaleInterval of 10^k type
    while (result > maxNumberOfCells) {
        result = (maxValue + scaleInterval - 1) / scaleInterval
        scaleInterval *= 10
    }
    scaleInterval /= 10
    // try to divide scale on scaleExtensions constants
    val scaleExtensions = listOf(2, 4, 5)
    var scale = 1
    scaleExtensions.forEach {
        if (result * it < maxNumberOfCells) {
            scale = it
        }
    }
    val marksAmount = result * scale
    val interval = scaleInterval.toFloat() / (baseToInt * scale)
    val signs = signsAfterDot(interval)
    return Scale(marksAmount, interval, signs)
}

/**
 * Stores scale of axis:
 * @param marksAmount - number of marks on axis
 * @param interval - amount of values between neighbour marks
 * @param signs - number of signs after dot in interval
 */

data class Scale(val marksAmount: Int, val interval: Float, val signs: Int)
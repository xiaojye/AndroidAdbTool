package tool

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

/**
 * @auth 二宁
 * @date 2023/12/12
 */
fun String?.formatSize(multiple:Float = 1F):String{
    this ?: return "0 b"
    try {
        val value = (toLong() * multiple).toLong()
        return value.toFileSizeString()
    }catch (e:Exception){
        e.printStackTrace()
    }
    return "Error"
}

fun Long.toFileSizeString(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val unitIndex = (log10(abs(this).toDouble()) / log10(1024.0)).toInt()
    val scaledSize = abs(this) / (1024.0.pow(unitIndex))
    return String.format("%.2f %s", scaledSize, units[unitIndex])
}
package cn.erning.adbutil.tool

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author erning
 * @date 2022/7/11 12:49
 * des:
 */
object CLUtil {
    fun execute(command: String?): String {
        val process: Process
        val stringBuilder = StringBuilder()
        try {
            process = Runtime.getRuntime().exec(command)
            process.waitFor()
            val `in` = InputStreamReader(process.inputStream)
            val reader = BufferedReader(`in`)
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("执行命令：\n${command}\n返回结果：\n${stringBuilder}")
        return stringBuilder.toString()
    }

    fun execute(command: Array<String>?): String {
        val process: Process
        val stringBuilder = StringBuilder()
        try {
            process = Runtime.getRuntime().exec(command)
            process.waitFor()
            val `in` = InputStreamReader(process.inputStream)
            val reader = BufferedReader(`in`)
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("执行命令：\n${command?.joinToString(" ") { it }}\n返回结果：\n${stringBuilder}")
        return stringBuilder.toString()
    }
}
package tool

import java.util.*

object PlatformUtil {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").lowercase(Locale.getDefault()).startsWith("windows")
    }
}
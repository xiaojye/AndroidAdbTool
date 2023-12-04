package tool

object PlatformUtil {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").startsWith("windows",true)
    }
}
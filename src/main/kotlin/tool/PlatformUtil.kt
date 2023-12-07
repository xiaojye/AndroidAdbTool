package tool

import java.awt.Desktop
import java.net.URI
import java.util.*

object PlatformUtil {
    fun isWindows(): Boolean {
        return System.getProperties().getProperty("os.name").contains("windows",true)
    }
    fun isMac(): Boolean {
        return System.getProperties().getProperty("os.name").contains("mac",true)
    }

    fun openBrowser(url: String){
        try {
            Desktop.getDesktop().browse(URI.create(url))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
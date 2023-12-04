package tool

import BundleUtilJava
import bean.DeviceInfo
import com.android.tools.build.bundletool.BundleToolMain
import exit.ExitException
import exit.NoExitSecurityManager

/**
 * @auth 二宁
 * @date 2023/12/4
 */
object BundleUtil {
    fun aab2apks(aabPath:String,apksPath:String,jksPath:String,jksPass:String,jksAlias:String,jksAliasPass:String){
        val args = arrayOfNulls<String>(7)
        args[0] = "build-apks"
        args[1] = "--bundle=$aabPath"
        args[2] = "--output=$apksPath"
        args[3] = "--ks=$jksPath"
        args[4] = "--ks-pass=pass:$jksPass"
        args[5] = "--ks-key-alias=$jksAlias"
        args[6] = "--key-pass=pass:$jksAliasPass"

        val sm = System.getSecurityManager()
        try {
            System.setSecurityManager(NoExitSecurityManager())
            BundleToolMain.main(args)
        } catch (ignore: ExitException) {
        }finally {
            System.setSecurityManager(sm)
            BundleUtilJava.resetAdbServer()
        }
    }

    fun installApks(device: DeviceInfo, apksPath:String){
        val command = arrayOf("install-apks","--apks=${apksPath}","--adb=${ADBUtil.ADB_PATH}","--device-id=${device.device}")

        val sm = System.getSecurityManager()
        try {
            System.setSecurityManager(NoExitSecurityManager())
            BundleToolMain.main(command)
        } catch (ignore: ExitException) {
        }finally {
            System.setSecurityManager(sm)
            BundleUtilJava.resetAdbServer()
        }
    }
}
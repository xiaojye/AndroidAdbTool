package tool

import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.swing.filechooser.FileSystemView

/**
 * @auth 二宁
 * @date 2023/11/3
 */
object FileUtil {
    fun getSelfPath(): String {
        // 方法一：获取当前可执行jar包所在目录
        var filePath = System.getProperty("java.class.path")
        // 得到当前操作系统的分隔符，windows下是";",linux下是":"
        val pathSplit = System.getProperty("path.separator")
        // 若没有其他依赖，则filePath的结果应当是该可运行jar包的绝对路径，此时我们只需要经过字符串解析，便可得到jar所在目录
        if (filePath.contains(pathSplit)) {
            val paths = filePath.split(pathSplit)
            for (i in paths.indices) {
                val f = File(paths[i])
                if (f.exists()){
                    // 只要目录，不精确到文件
                    filePath = if(f.isFile) f.parentFile.absolutePath else f.absolutePath
                    break
                }
            }
        } else if (filePath.endsWith(".jar")) {
            // 截取路径中的jar包名,可执行jar包运行的结果里包含".jar"
            // 有时候会直接返回文件名，过滤之
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1)
        }
        if (filePath.isBlank()){
            filePath = System.getProperty("user.dir")
        }
        println("jar包所在目录：$filePath")
        return filePath
    }

    fun releaseAdb(){
        val adbDir = File(getSelfPath(),"runtimeAdbFiles")
        if(adbDir.isFile){
            adbDir.delete()
        }
        if (!adbDir.exists()){
            adbDir.mkdirs()
            adbDir.setWritable(true,false)
            if (System.getProperties().getProperty("os.name").lowercase(Locale.getDefault()).startsWith("windows")){
                releaseFile(adbDir,"adb.exe","bin"+File.separator+"adb.exe")
                releaseFile(adbDir,"AdbWinApi.dll","bin"+File.separator+"AdbWinApi.dll")
                releaseFile(adbDir,"AdbWinUsbApi.dll","bin"+File.separator+"AdbWinUsbApi.dll")
            }else{
                releaseFile(adbDir,"adb","bin"+File.separator+"adb")
            }
        }
    }

    private fun releaseFile(dir:File,fileName:String,packageFile:String){
        val file = File(dir,fileName)
        if (!file.exists()){
            ClassLoader.getSystemResourceAsStream(packageFile)?.use {
                file.createNewFile()
                file.writeBytes(it.readAllBytes())
            }
            file.setExecutable(true,false)
        }
    }

    fun getCacheDir(): File {
        val cacheDir = File(getSelfPath(),"runtimeCache")
        if(cacheDir.isFile){
            cacheDir.delete()
        }
        if (!cacheDir.exists()){
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    fun cleanCache(){
        getCacheDir().deleteRecursively()
    }

    fun openFileWithDefault(file:File?){
        file ?: return
        if(!file.exists()){
            return
        }
        try {
            Desktop.getDesktop().open(file)
        }catch (ignore:Exception){
            openFileInExplorer(file)
        }
    }

    fun openFileInExplorer(file:File?){
        file ?: return
        if(!file.exists()){
            return
        }
        try {
            Desktop.getDesktop().browseFileDirectory(file)
        }catch (ignore:Exception){ }
    }

    fun getDesktopFile(): File {
        return File(FileSystemView.getFileSystemView().homeDirectory,"Desktop")
    }
}
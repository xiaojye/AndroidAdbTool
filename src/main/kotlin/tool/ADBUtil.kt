package tool

import bean.DeviceInfo
import bean.FileBean
import java.io.File
import java.util.*

/**
 * @author erning
 * @date 2022/7/8 14:37
 */
object ADBUtil {
    private var ADB_PATH = getAdbPath()

    /**
     * 检查有没有ADB
     */
    fun checkADB(): Boolean {
        val command = arrayOf("--version")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        val versionLine = getLineWithStart("Version",data)
        return versionLine != null
    }

    /**
     * 获取设备列表
     */
    fun getDevice(): ArrayList<DeviceInfo> {
        val command = arrayOf("devices","-l")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        val list = arrayListOf<DeviceInfo>()
        data.forEachIndexed { index, arr ->
            if (index != 0){
                val id = arr.getOrNull(0)?.split(":")?.firstOrNull() ?: ""
                val name = arr.getOrNull(4)?.split(":")?.getOrNull(1) ?: ""
                val model = arr.getOrNull(3)?.split(":")?.getOrNull(1) ?: ""
                val device = DeviceInfo(name,model,id)
                list.add(device)
            }
        }
        return list
    }

    /**
     * 打开tcpip 5555
     */
    fun openWIFIConnect(deviceId: String): Boolean {
        val command = arrayOf("-s",deviceId,"tcpip","5555")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        return data.firstOrNull()?.firstOrNull()?.equals("restarting",true) ?: false
    }

    /**
     * 连接设备
     */
    fun connectDevice(deviceId: String): Boolean {
        val command = arrayOf("connect",deviceId)
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        return data.firstOrNull()?.firstOrNull()?.equals("connected",true) ?: false
    }

    /**
     * 断开设备
     */
    fun disconnectDevice(deviceId: String): Boolean {
        val command = arrayOf("disconnect",deviceId)
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        return data.firstOrNull()?.firstOrNull()?.equals("disconnected",true) ?: false
    }

    /**
     * 安装apk，结果如下
     * Performing Streamed Install
     * Success
     * 或
     * INSTALL_FAILED_DEPRECATED_SDK_VERSION: App package must target at least SDK version 23, but found 7
     */
    fun install(deviceId:String,filePath:String){
        val command = arrayOf("-s",deviceId,"install",filePath)
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        if(data.getOrNull(1)?.firstOrNull()?.equals("success",true) != true){
            if (data.joinToString(" ").contains("INSTALL_FAILED_DEPRECATED_SDK_VERSION")){
                installLow(deviceId, filePath)
            }else{
                throw RuntimeException(data.joinToString(" \\n "){
                    it.joinToString(" ")
                })
            }
        }
    }

    /**
     * 安装用于目标版本小于6.0的应用
     */
    private fun installLow(deviceId:String,filePath:String){
        val command = arrayOf("-s",deviceId,"install","--bypass-low-target-sdk-block",filePath)
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        if(data.getOrNull(1)?.firstOrNull()?.equals("success",true) != true){
            throw RuntimeException(data.joinToString(" \\n "){
                it.joinToString(" ")
            })
        }
    }

    /**
     * 获取wlan0的ip地址
     * adb返回内容如下：
     * 13: wlan0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 3000
     *     link/ether c6:66:99:c5:fa:97 brd ff:ff:ff:ff:ff:ff
     *     inet 10.200.89.54/23 brd 10.200.89.255 scope global wlan0
     *         valid_lft forever preferred_lft forever
     *     inet6 fe80::c466:99ff:fec5:fa97/64 scope link
     *         valid_lft forever preferred_lft forever
     */
    fun getWlan0IP(deviceId:String,v4:Boolean = true): String? {
        val command = arrayOf("-s",deviceId,"shell","ip","addr","show","wlan0","|","grep","'inet'","|","cut","-d","'/'","-f","1")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        return if (v4){
            data.getOrNull(0)?.lastOrNull()
        }else{
            data.getOrNull(1)?.lastOrNull()
        }
    }

    /**
     * 获取Mac地址
     */
    fun getMac(deviceId:String): String {
        val command = arrayOf("-s",deviceId,"shell","ip","addr","show","wlan0","|","grep","'link/ether'")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        return data.getOrNull(0)?.getOrNull(1) ?: ""
    }

    /**
     * 获取AndroidId
     */
    fun getAndroidId(deviceId:String): String {
        val command = arrayOf("-s",deviceId,"shell","settings","get","secure","android_id")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        return result
    }

    /**
     * 获取应用安装路径
     */
    fun getApkPath(deviceId: String, pk: String): String? {
        val command = arrayOf("-s", deviceId, "shell", "pm", "path", pk)
        return CLUtil.execute(arrayOf(ADB_PATH, *command)).split(":").lastOrNull()?.replace("\n","")?.replace("\r","")
    }

    /**
     * 获取当前页面
     */
    fun getCurrentActivity(deviceId: String): String {
        val command = arrayOf("-s", deviceId, "shell", "dumpsys", "window", "|", "grep", "mCurrentFocus")
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        val windows = data.joinToString("\n") {
            (it.lastOrNull() ?: "").replace("}","")
        }
        return windows
    }

    /**
     * 停止应用
     */
    fun stopApplication(deviceId: String, pk: String) {
        val command = arrayOf("-s", deviceId, "shell", "am", "force-stop", pk)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 卸载
     */
    fun unInstall(deviceId: String, pk: String) {
        val command = arrayOf("-s", deviceId, "uninstall", pk)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 清空数据
     * FIXME: 似乎没有正常工作
     */
    fun cleanAppData(deviceId: String, pk: String) {
        stopApplication(deviceId, pk)
        val command = arrayOf("-s", deviceId, "shell","pm","clear", pk)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 输入文本
     */
    fun inputText(deviceId: String, text: String){
        val command = arrayOf("-s", deviceId, "shell","input","text", text)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 输入按键
     * Home:3 返回:4 增加音量:24 降低音量:25 电源:26 菜单:82 静音:164 切换应用:187
     */
    fun inputKey(deviceId: String, event: String){
        val command = arrayOf("-s", deviceId, "shell","input","keyevent", event)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 导出文件
     */
    fun exportFile(deviceId: String,deviceFile: String, localFile: String) {
        val command = arrayOf("-s", deviceId, "pull", deviceFile, localFile)
        CLUtil.execute(arrayOf(ADB_PATH, *command))
    }

    /**
     * 列出文件
     */
    fun fileList(deviceId: String,dir:String): ArrayList<FileBean> {
        val command = arrayOf("-s", deviceId, "shell","ls","-FA", dir)
        val result = CLUtil.execute(arrayOf(ADB_PATH, *command))
        val data = parseResult(result)
        val list = arrayListOf<FileBean>()
        data.forEach {
            val line = it.joinToString("") { it }
            val isDir = line.endsWith("/")
            val bean = FileBean(if (isDir) line.removeSuffix("/") else line, isDir)
            list.add(bean)
        }
        return list
    }

    /**
     * 获取ADB路径
     */
    private fun getAdbPath(): String {
        var adbPath: String = if (System.getProperties().getProperty("os.name").lowercase(Locale.getDefault()).startsWith("windows")) {
            File(FileUtil.getSelfPath()).absolutePath + "\\adb.exe"
        } else {
            File(FileUtil.getSelfPath()).absolutePath + "/adb"
        }
        if (File(adbPath).exists()) {
            println("找到ADB文件：$adbPath")
            return adbPath
        }
        println("ADB文件不存在，使用环境变量")
        adbPath = "adb"
        return adbPath
    }

    private fun getLineWithStart(start:String,data:Array<Array<String>>,ignoreCase:Boolean = true): Array<String>? {
        data.forEach { line ->
            if(ignoreCase){
                if(start.equals(line.firstOrNull(),true)){
                    return line
                }
            }else{
                if(line.firstOrNull() == start){
                    return line
                }
            }
        }
        return null
    }

    private fun parseResult(result:String?): Array<Array<String>> {
        result ?: return emptyArray()

        val data = arrayListOf<Array<String>>()

        val lines = result.replace("\r","\n").split("\n")
        lines.forEach {
            val line = parseLine(it)
            if(line.isNotEmpty()) {
                data.add(line)
            }
        }

        return data.toTypedArray()
    }

    private fun parseLine(line:String?):Array<String>{
        if(line == null || line.isBlank()){
            return emptyArray()
        }
        val lines = line.replace("\t"," ").split(" ").filter { it.trim().isNotBlank() }
        return lines.toTypedArray()
    }
}
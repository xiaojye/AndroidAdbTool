package page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import bean.DeviceInfo
import tool.ADBUtil
import java.awt.FileDialog
import java.io.File

/**
 * @auth 二宁
 * @date 2023/12/4
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun InstallPage(device: DeviceInfo){
    var apkFile by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(10.dp).fillMaxWidth().padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                val fileDialog = FileDialog(ComposeWindow(),"安装应用", FileDialog.LOAD)
                fileDialog.isMultipleMode = false
                fileDialog.isVisible = true
                val directory = fileDialog.directory?.replace("\n","")?.replace("\r","")
                val file = fileDialog.file?.replace("\n","")?.replace("\r","")
                if(directory.isNullOrBlank() || file.isNullOrBlank()){
                    return@Button
                }
                apkFile = File(directory,file).absolutePath
                installApp(device,File(directory,file))
            }, modifier = Modifier.padding(10.dp)) {
                Text("选择文件")
            }
            TextField(
                modifier = Modifier.fillMaxWidth().onExternalDrag(
                    onDrop = {
                        if(it.dragData is DragData.FilesList){
                            val files = (it.dragData as DragData.FilesList).readFiles()
                            if(files.size != 1){
                                return@onExternalDrag
                            }
                            val file = files.first()
                            apkFile = file.removePrefix("file:")
                        }
                    }
                ),
                value = apkFile ?: "" ,
                onValueChange = {apkFile=it},
                singleLine = true,
                placeholder = { Text("支持apk、apks、xapk、aab，支持拖拽") },
                isError = apkFile.isNullOrBlank() || (!apkFile!!.endsWith(".apk",true) && !apkFile!!.endsWith(".apks",true) || !apkFile!!.endsWith(".xapk",true) || !apkFile!!.endsWith(".aab",true))
            )
        }
        Button(onClick = {
            installApp(device,File(apkFile ?: ""))
        }, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Text("安装")
        }
    }
}

private fun installApp(device:DeviceInfo,file: File?){
    println(file?.absolutePath)
    file ?: return
    if(!file.exists()){
        return
    }

    if(file.name.endsWith(".apk",true)){
        ADBUtil.install(device.device,file.absolutePath)
    }
}
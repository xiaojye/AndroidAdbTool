package page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dialog.MessageDialog
import res.whiteColor
import tool.ADBUtil

@Composable
fun InfoPage(deviceId: String) {
    var refreshCount by remember { mutableStateOf(0) }
    var showUnInstallDialog by remember { mutableStateOf(false) }
    var showCleanDataDialog by remember { mutableStateOf(false) }

    var currentActivity by remember { mutableStateOf("") }
    var currentPackageName by remember { mutableStateOf("") }
    var currentApkPath by remember { mutableStateOf("") }

    LaunchedEffect(refreshCount,deviceId){
        currentActivity = ADBUtil.getCurrentActivity(deviceId)
        currentPackageName = currentActivity.split("/").getOrNull(0) ?: ""
        currentApkPath = ADBUtil.getApkPath(deviceId,currentPackageName) ?: ""
    }
    Column {
        Surface(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row {
                    Text("当前应用包名：")
                    Text(currentPackageName)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Text("当前页面：")
                    Text(currentActivity)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Text("当前应用安装路径：")
                    Text(currentApkPath)
                }
            }
        }
        Row {
            Button(onClick = {
                ADBUtil.stopApplication(deviceId,currentPackageName)
            }, modifier = Modifier.padding(10.dp)) {
                Text("停止运行")
            }
            Button(onClick = {
                showUnInstallDialog = true
            }, modifier = Modifier.padding(10.dp)) {
                Text("卸载应用")
            }
            Button(onClick = {
                showCleanDataDialog = true
            }, modifier = Modifier.padding(10.dp)) {
                Text("清空数据")
            }
            Button(onClick = {
                             // TODO
            }, modifier = Modifier.padding(10.dp)) {
                Text("导出安装包")
            }
        }
        Button(onClick = {
            refreshCount++
        }, modifier = Modifier.padding(10.dp).fillMaxWidth()) {
            Text("刷新数据")
        }
    }

    if(showUnInstallDialog) {
        MessageDialog("注意", "确定要卸载吗？", {
            showUnInstallDialog = false
        }, {
            showUnInstallDialog = false
            ADBUtil.unInstall(deviceId,currentPackageName)
            refreshCount++
        })
    }
    if(showCleanDataDialog) {
        MessageDialog("注意", "确定要清空数据吗？", {
            showCleanDataDialog = false
        }, {
            showCleanDataDialog = false
            ADBUtil.cleanAppData(deviceId,currentPackageName)
            refreshCount++
        })
    }
}
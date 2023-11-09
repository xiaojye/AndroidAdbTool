package page

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dialog.InputDialog
import res.randomColor
import tool.ADBUtil
import tool.CLUtil
import tool.FileUtil
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * 快捷功能
 */
@Composable
fun QuickPage(device: String) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight().padding(horizontal = 10.dp, vertical = 10.dp).fillMaxWidth(),
    ) {
        // 前后加个间距
        item {
            CommonFunction(device)
            Spacer(modifier = Modifier.height(10.dp))
            AboutSystem(device)
            Spacer(modifier = Modifier.height(10.dp))
            AboutKeyBoard(device)
        }
    }
}

/**
 * 常用功能
 */
@Composable
private fun CommonFunction(device: String) {
    var showInputTextDialog by remember { mutableStateOf(false) }

    BaseQuick("常用功能", color = Color(255, 152, 0)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem("icon/ic_install.svg", "安装应用（外部）", modifier = Modifier.weight(1f).clickable {
                CLUtil.execute(arrayOf("java","-jar","/Users/erning/Program/Java/ApkInstaller_jar/ApkInstaller.jar"))
            })
            QuickItem("icon/ic_input.svg", "输入文本", modifier = Modifier.weight(1f).clickable {
                showInputTextDialog = true
            })
            QuickItem("icon/ic_cut_screen.svg", "截图保存到桌面", modifier = Modifier.weight(1f).clickable {
                val deviceFile = ADBUtil.screenshot(device)
                val localFile = File(FileUtil.getDesktopFile(),deviceFile.split("/").last()).absolutePath
                ADBUtil.pull(device,deviceFile,localFile)
                ADBUtil.deleteFile(device,deviceFile)
            })
            QuickItem(modifier = Modifier.weight(1f))
        }
    }

    if(showInputTextDialog) {
        InputDialog("请输入", "", {
            showInputTextDialog = false
        }, {
            showInputTextDialog = false
            it?.let {
                ADBUtil.inputText(device,it)
            }
        })
    }
}

/**
 * 系统相关
 */
@Composable
private fun AboutSystem(device: String) {
    BaseQuick("系统相关", color = Color(255, 193, 7)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem("icon/ic_reboot.svg", "重启手机", modifier = Modifier.weight(1f).clickable { ADBUtil.reboot(device,ADBUtil.RebootType.SYSTEM) })
            QuickItem("icon/ic_recover.svg", "重启到Recover", modifier = Modifier.weight(1f).clickable { ADBUtil.reboot(device,ADBUtil.RebootType.RECOVER) })
            QuickItem("icon/ic_fastboot.svg", "重启到Fastboot", modifier = Modifier.weight(1f).clickable { ADBUtil.reboot(device,ADBUtil.RebootType.FASTBOOT) })
            QuickItem(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 按键相关
 */
@Composable
private fun AboutKeyBoard(deviceId: String) {
    BaseQuick("按键相关", color = Color(158, 176, 184)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem("icon/ic_home.svg", "Home键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"3") })
            QuickItem("icon/ic_back.svg", "Back键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"4") })
            QuickItem("icon/ic_menu.svg", "Menu键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"82") })
            QuickItem("icon/ic_power.svg", "Power键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"26") })
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem("icon/ic_volume_add.svg", "增加音量", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"24") })
            QuickItem("icon/ic_volume_sub.svg", "降低音量", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"25") })
            QuickItem("icon/ic_volume_close.svg", "静音", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"164") })
            QuickItem("icon/ic_switch.svg", "切换应用", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"187") })
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem("icon/ic_top.svg", "向上滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"300","800","300","200") })
            QuickItem("icon/ic_down.svg", "向下滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"300","200","300","800") })
            QuickItem("icon/ic_left.svg", "向左滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"800","300","200","300") })
            QuickItem("icon/ic_right.svg", "向右滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"200","300","800","300") })
        }
    }
}

@Composable
private fun BaseQuick(title: String, color: Color = Color.Transparent, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.background(Color.White).fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.5.dp, Color(220, 220, 220)),  // 边框
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier.width(4.dp).height(16.dp).clip(RoundedCornerShape(8.dp)).background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title)
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun QuickItem(ttf: String? = null, title: String? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (ttf == null || title == null) return@Column
        Box(
            modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(
                randomColor[Random.nextInt(
                    IntRange(
                        0, randomColor.size - 1
                    )
                )]
            ),
        ) {
            Image(
                painter = painterResource(ttf),
                contentDescription = "",
                modifier = Modifier.padding(16.dp).width(38.dp).height(38.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MessageDialog2(title: String, content: String, clickDismiss: () -> Unit) {
    AlertDialog(modifier = Modifier.width(300.dp),
        onDismissRequest = {
            clickDismiss.invoke()
        }, buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = clickDismiss,
                ) {
                    Text("确定")
                }
            }
        }, title = {
            Text(title)
        }, text = {
            // text默认不支持复制黏贴 需要用SelectionContainer包裹
            SelectionContainer {
                Text(content)
            }
        })
}
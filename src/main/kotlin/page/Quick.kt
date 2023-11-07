package page

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dialog.InputDialog
import res.randomColor
import tool.ADBUtil
import tool.CLUtil
import tool.ttfFontFamily
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * 快捷功能
 */
@Composable
fun QuickPage(device: String) {
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }


    if (dialogContent.isNotEmpty()) {
        MessageDialog2(dialogTitle, dialogContent) {
            dialogTitle = ""
            dialogContent = ""
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp, vertical = 12.dp).fillMaxWidth(),
    ) {
        // 前后加个间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
            CommonFunction(device)
            Spacer(modifier = Modifier.height(16.dp))
            AboutSystem(device) { title, content ->
                dialogTitle = title
                dialogContent = content
            }
            Spacer(modifier = Modifier.height(16.dp))
            AboutKeyBoard(device)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
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
            QuickItem(0xe693, "安装应用（外部）", modifier = Modifier.weight(1f).clickable {
                CLUtil.execute(arrayOf("java","-jar","/Users/erning/Program/Java/ApkInstaller_jar/ApkInstaller.jar"))
            })
            QuickItem(0xe816, "输入文本", modifier = Modifier.weight(1f).clickable {
                showInputTextDialog = true
            })
            QuickItem(0xe931, "截图保存到桌面", modifier = Modifier.weight(1f))
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
private fun AboutSystem(device: String, onClick: (title: String, content: String) -> Unit) {
    BaseQuick("系统相关", color = Color(255, 193, 7)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem(0xe695, "开始录屏", modifier = Modifier.weight(1f))
            QuickItem(0xe71d, "结束录屏保存到桌面", modifier = Modifier.weight(1f))
            QuickItem(0xe632, "查看IP地址", modifier = Modifier.weight(1f).clickable {
                val ipv4 = ADBUtil.getWlan0IP(device,true)
                onClick.invoke("查看IP地址", "$ipv4")
            })
            QuickItem(0xe65d, "查看Mac地址", modifier = Modifier.weight(1f).clickable {
                onClick.invoke("查看Mac地址", ADBUtil.getMac(device))
            })
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem(0xe6b2, "重启手机", modifier = Modifier.weight(1f))
            QuickItem(0xe6b2, "重启到Recover", modifier = Modifier.weight(1f))
            QuickItem(0xe6b2, "重启到Fastboot", modifier = Modifier.weight(1f))
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
            QuickItem(0xe68e, "Home键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"3") })
            QuickItem(0xe616, "Back键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"4") })
            QuickItem(0xe605, "Menu键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"82") })
            QuickItem(0xe615, "Power键", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"26") })
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem(0xe76e, "增加音量", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"24") })
            QuickItem(0xe771, "降低音量", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"25") })
            QuickItem(0xe612, "静音", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"164") })
            QuickItem(0xe658, "切换应用", modifier = Modifier.weight(1f).clickable { ADBUtil.inputKey(deviceId,"187") })
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            QuickItem(0xe795, "向上滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"300","800","300","200") })
            QuickItem(0xe603, "向下滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"300","200","300","800") })
            QuickItem(0xe603, "向左滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"800","300","200","300") })
            QuickItem(0xe60a, "向右滑动", modifier = Modifier.weight(1f).clickable { ADBUtil.inputSwipe(deviceId,"200","300","800","300") })
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
private fun QuickItem(ttf: Int? = null, title: String? = null, modifier: Modifier = Modifier) {
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
            Text(
                text = "${Char(ttf)}",
                fontFamily = ttfFontFamily(),
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
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
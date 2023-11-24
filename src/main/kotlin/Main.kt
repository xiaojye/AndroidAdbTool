import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bean.DeviceInfo
import bean.createMainNavData
import config.DeviceRecordUtil
import page.*
import res.defaultBgColor
import tool.ADBUtil
import tool.FileUtil
import java.awt.Dimension
import javax.swing.UIManager

fun main() = application {
    FileUtil.releaseAdb()
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    DeviceRecordUtil.readDeviceList()
    Window(
        onCloseRequest = {
            FileUtil.cleanCache()
            exitApplication()
        },
        title = "AndroidAdbTool(${this::class.java.`package`.implementationVersion})",
        visible = true,
        state = WindowState(
            size = DpSize(width = 1200.dp, height = 900.dp),
            position = WindowPosition(Alignment.Center)
        )
    ) {
        window.minimumSize = Dimension(680, 400)
        App()
    }
}

@Composable
@Preview
fun App() {
    val connectedDevicesList = remember { mutableStateListOf<DeviceInfo>() }
    val refreshConnectedDevicesList:()->Unit = {
        connectedDevicesList.clear()
        connectedDevicesList.addAll(ADBUtil.getDevice())
        DeviceRecordUtil.saveAll(connectedDevicesList)
    }

    var selectItem by remember { mutableStateOf(0) }
    var device by remember { mutableStateOf<DeviceInfo?>(null) }

    Scaffold(bottomBar = {
        MainNav(modifier = Modifier.width(200.dp),refreshConnectedDevicesList,connectedDevicesList, { selectItem = it }) {
            if (it != null) {
                device = it
            }
        }
    }) {
        Box(modifier = Modifier.background(defaultBgColor).fillMaxHeight().fillMaxWidth().padding(start = 200.dp)) {
            if(device != null){
                when (selectItem) {
                    0 -> CurrentAppInfoPage(device!!)
                    1 -> PhoneInfoPage(device!!,refreshConnectedDevicesList)
                    2 -> QuickPage(device!!)
                    3 -> FileManager(device!!, ADBUtil.hasRoot(device!!.device))
                    4 -> DeviceRecordPage(refreshConnectedDevicesList,connectedDevicesList)
                }
            }
        }
    }
}

@Composable
private fun MainNav(modifier: Modifier,refreshConnectedDevicesList:()->Unit,connectedDevicesList:MutableList<DeviceInfo>, onSelectItem: (Int) -> Unit, deviceId: (DeviceInfo?) -> Unit) {
    var navigationIndex by remember { mutableStateOf(0) }
    NavigationRail(modifier, elevation = 0.dp, header = {
        Spacer(modifier = Modifier.height(12.dp))
        ConnectDevices(refreshConnectedDevicesList,connectedDevicesList,deviceId)
    }) {
        createMainNavData().forEachIndexed { index, mainNavBean ->
            MainNavItem(navigationIndex, index, mainNavBean.svgName, mainNavBean.labelText) {
                navigationIndex = it
                onSelectItem.invoke(navigationIndex)
            }
        }
    }
}

/**
 *
 * 首页导航
 * @param selectedPosition 当前选中的position
 * @param mePosition 自己的position
 */
@Composable
private fun MainNavItem(selectedPosition: Int, mePosition: Int, svgPath: String, labelText: String, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            onClick.invoke(mePosition)
        }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(svgPath), "", modifier = Modifier.width(24.dp).height(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            labelText, color = if (selectedPosition == mePosition) Color(
                139, 195, 74
            ) else Color.Gray
        )
    }
}

/**
 * 连接设备widget
 */
@Composable
fun ConnectDevices(refreshConnectedDevicesList:()->Unit,connectedDevicesList:MutableList<DeviceInfo>,deviceCallback: (DeviceInfo?) -> Unit) {
    // 拿到已经连接的所有设备
    var refresh by remember { mutableStateOf(0) }
    LaunchedEffect(refresh) {
        refreshConnectedDevicesList()
    }

    var showDeviceItem by remember { mutableStateOf(false) }
    val size by animateDpAsState(
        targetValue = if (showDeviceItem) 120.dp else 0.dp
    )
    // 箭头旋转动画
    val arrowAnim by animateFloatAsState(if (showDeviceItem) -180f else 0f)
    var selectIndexDevice by remember { mutableStateOf(0) }
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        val deviceName = if (connectedDevicesList.isNotEmpty()) {
            val firstDevice = connectedDevicesList[selectIndexDevice]
            // 获取设备品牌
            deviceCallback.invoke(firstDevice)
            "${firstDevice.deviceName}\n${firstDevice.deviceModel}"
        } else {
            deviceCallback.invoke(null)
            "连接设备"
        }
        TextButton(
            { showDeviceItem = !showDeviceItem },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(19.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(deviceName, color = Color.Black)
            Icon(Icons.Default.ArrowDropDown,
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier.graphicsLayer {
                    rotationX = arrowAnim
                })
        }
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(size != 0.dp) {
            LazyColumn {
                items(connectedDevicesList.size) {
                    val device = connectedDevicesList[it]
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectIndexDevice = it
                            showDeviceItem = false
                            deviceCallback.invoke(device)
                        }.padding(vertical = 6.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${device.deviceName}\n${device.deviceModel}",
                            color = if (deviceName.contains(device.deviceName) || deviceName.contains(device.deviceModel)) Color(
                                139, 195, 74
                            ) else Color.Black
                        )
                        if (deviceName.contains(device.deviceName) || deviceName.contains(device.deviceModel)) {
                            Icon(Icons.Default.Check, contentDescription = "", tint = Color(139, 195, 74))
                        }
                    }
                }
            }
        }

        Button(onClick = {
            connectedDevicesList.clear()
            selectIndexDevice = 0
            refresh++
        }, modifier = Modifier.fillMaxWidth()) {
            Text("刷新Adb Device")
        }
    }
}
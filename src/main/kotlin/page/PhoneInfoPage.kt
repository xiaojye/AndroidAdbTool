package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bean.DeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tool.ADBUtil


@Composable
fun PhoneInfoPage(device: DeviceInfo) {
    var map by remember { mutableStateOf<Map<String,String>>(mapOf()) }
    var refreshCount by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(device.device,refreshCount,map){
        withContext(Dispatchers.IO) {
            val newMap = ADBUtil.getProp(device.device)
            newMap["androidId"] = ADBUtil.getAndroidId(device.device)
            newMap["density"] = ADBUtil.getDensity(device.device)
            newMap["physicalSize"] = ADBUtil.getPhysicalSize(device.device)
            newMap["mac"] = ADBUtil.getMac(device.device)
            newMap["ipv4"] = ADBUtil.getWlan0IP(device.device, true) ?: ""
            val batteryInfo = ADBUtil.getBatteryInfo(device.device)
            newMap["batteryInfo-statusStr"] = batteryInfo.getStatusStr()
            newMap["batteryInfo-healthStr"] = batteryInfo.getHealthStr()
            newMap["batteryInfo-chargingMethod"] = batteryInfo.getChargingMethod()
            newMap["batteryInfo-present"] = batteryInfo.present.toString()
            newMap["batteryInfo-level"] = "${batteryInfo.level}/${batteryInfo.scale}"
            newMap["batteryInfo-voltage"] = batteryInfo.voltage.toString()
            newMap["batteryInfo-temperature"] = batteryInfo.temperature.toString()
            newMap["batteryInfo-technology"] = batteryInfo.technology ?: ""
            newMap["batteryInfo-counter"] = batteryInfo.counter.toString()
            withContext(Dispatchers.Default){
                map = newMap
            }
        }
    }
    Column(modifier = Modifier.padding(10.dp).fillMaxWidth().background(Color.White).padding(10.dp).verticalScroll(scrollState)) {
        SelectionContainer {
            Column {
                Text("生产商："+map["ro.product.manufacturer"])
                Text("品牌："+map["ro.product.brand"])
                Text("型号："+map["ro.product.model"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("Android版本："+map["ro.build.version.release"])
                Text("AndroidId："+map["androidId"])
                Text("SDK版本："+map["ro.build.version.sdk"])
                Text("设备指纹："+map["ro.build.fingerprint"])
                Text("安全补丁更新日期："+map["ro.build.version.security_patch"])
                Text("Debug："+map["ro.debuggable"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("分辨率："+map["physicalSize"])
                Text("DPI："+map["density"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("IP："+map["ipv4"])
                Text("MAC："+map["mac"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("地区："+map["ro.product.locale"])
                Text("时区："+map["persist.sys.timezone"])
                Text("国家代码："+map["gsm.operator.iso-country"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("Abi列表："+map["ro.product.cpu.abilist"])
                Spacer(modifier = Modifier.height(10.dp))
                Text("电池存在："+map["batteryInfo-present"])
                Text("电池状态："+map["batteryInfo-statusStr"])
                Text("电池电量："+map["batteryInfo-level"])
                Text("电池电压："+map["batteryInfo-voltage"])
                Text("电池温度："+map["batteryInfo-temperature"])
                Text("充电方式："+map["batteryInfo-chargingMethod"])
                Text("电池健康："+map["batteryInfo-healthStr"])
                Text("充电计数："+map["batteryInfo-counter"])
                Text("电池技术："+map["batteryInfo-technology"])
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            refreshCount++
        }, modifier = Modifier.padding(10.dp).fillMaxWidth()) {
            Text("刷新数据")
        }
    }
}
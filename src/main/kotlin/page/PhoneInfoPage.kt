package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tool.ADBUtil


@Composable
fun PhoneInfoPage(deviceId: String) {
    var map by remember { mutableStateOf<Map<String,String>>(mapOf()) }

    LaunchedEffect(deviceId,map){
        val newMap = ADBUtil.getProp(deviceId)
        newMap["androidId"] = ADBUtil.getAndroidId(deviceId)
        newMap["density"] = ADBUtil.getDensity(deviceId)
        newMap["physicalSize"] = ADBUtil.getPhysicalSize(deviceId)
        newMap["mac"] = ADBUtil.getMac(deviceId)
        newMap["ipv4"] = ADBUtil.getWlan0IP(deviceId,true) ?: ""
        map = newMap
    }
    Column(modifier = Modifier.padding(10.dp).fillMaxWidth().background(Color.White).padding(10.dp)) {
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
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Button(onClick = {
                ADBUtil.disconnectDevice(deviceId)
            }, modifier = Modifier.padding(10.dp)) {
                Text("断开连接")
            }
        }
    }
}
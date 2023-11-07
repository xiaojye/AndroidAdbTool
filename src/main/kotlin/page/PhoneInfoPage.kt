package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bean.FileBean
import tool.ADBUtil
import java.text.SimpleDateFormat

@Composable
fun PhoneInfoPage(deviceId: String) {
    var map by remember { mutableStateOf<Map<String,String>>(mapOf()) }
    var androidId by remember { mutableStateOf("") }
    var density by remember { mutableStateOf("") }
    var physicalSize by remember { mutableStateOf("") }

    LaunchedEffect(deviceId,androidId,density,physicalSize,map){
        androidId = ADBUtil.getAndroidId(deviceId)
        density = ADBUtil.getDensity(deviceId)
        physicalSize = ADBUtil.getPhysicalSize(deviceId)
        map = ADBUtil.getProp(deviceId)
    }
    Column(modifier = Modifier.padding(10.dp).fillMaxWidth().background(Color.White).padding(10.dp)) {
        Text("生产商："+map["ro.product.manufacturer"])
        Text("品牌："+map["ro.product.brand"])
        Text("型号："+map["ro.product.model"])
        Text("设备型号："+map["ro.product.marketname"])
        Text("UI版本："+map["ro.product.build.version.incremental"])
        Text("设备名称："+map["persist.sys.device_name"])
        val timeStr = map["ro.product.build.date.utc"]?.trim()
        val time = if(timeStr.isNullOrBlank()) System.currentTimeMillis() else "${timeStr}000".toLongOrNull()
        Text("生产时间："+SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time))
        Spacer(modifier = Modifier.height(10.dp))
        Text("Android版本："+map["ro.build.version.release"])
        Text("AndroidId：$androidId")
        Text("SDK版本："+map["ro.product.build.version.sdk"])
        Text("设备指纹："+map["ro.build.fingerprint"])
        Text("安全补丁更新日期："+map["ro.build.version.security_patch"])
        Text("Debug："+map["ro.debuggable"])
        Text("锁定状态："+map["ro.secureboot.lockstate"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("分辨率：$physicalSize")
        Text("Dpi：$density")
        Spacer(modifier = Modifier.height(10.dp))
        Text("地区："+map["ro.product.locale"])
        Text("时区："+map["persist.sys.timezone"])
        Text("国家代码："+map["gsm.operator.iso-country"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("定位类型："+map["ro.config.gnss.support"])
        Text("位置模拟："+map["ro.allow.mock.location"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("移动数据："+map["ro.com.android.mobiledata"])
        Text("数据漫游："+map["ro.com.android.dataroaming"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("IMEI："+map["ro.ril.oem.imei"])
        Text("IMEI 1："+map["ro.ril.oem.imei1"])
        Text("IMEI 2："+map["ro.ril.oem.imei2"])
        Text("MEDI："+map["ro.ril.oem.meid"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("Abi列表："+map["ro.product.cpu.abilist"])
        Spacer(modifier = Modifier.height(10.dp))
        Text("最后相机使用者："+map["persist.vendor.camera.clientname"])
    }
}
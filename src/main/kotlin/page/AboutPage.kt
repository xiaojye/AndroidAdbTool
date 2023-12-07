package page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tool.PlatformUtil
import java.awt.Desktop
import java.net.URI

/**
 * @auth 二宁
 * @date 2023/12/7
 */
@Composable
fun AboutPage(){
    Column(modifier = Modifier.padding(10.dp).fillMaxWidth().background(Color.White).padding(10.dp).verticalScroll(rememberScrollState())) {
        Row {
            Text(text = "下载ADB（中国官方）：")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://googledownloads.cn/android/repository/platform-tools-latest-windows.zip") }, text = "Windows版本", color = Color.Blue)
            Text(text = "、")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://googledownloads.cn/android/repository/platform-tools-latest-darwin.zip") }, text = "Mac版本", color = Color.Blue)
            Text(text = "、")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://googledownloads.cn/android/repository/platform-tools-latest-linux.zip") }, text = "Linux版本", color = Color.Blue)
        }
        Row(modifier = Modifier.padding(top=10.dp)) {
            Text(text = "下载ADB（官方）：")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://dl.google.com/android/repository/platform-tools-latest-windows.zip") }, text = "Windows版本", color = Color.Blue)
            Text(text = "、")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://dl.google.com/android/repository/platform-tools-latest-darwin.zip") }, text = "Mac版本", color = Color.Blue)
            Text(text = "、")
            Text(modifier = Modifier.clickable { PlatformUtil.openBrowser("https://dl.google.com/android/repository/platform-tools-latest-linux.zip") }, text = "Linux版本", color = Color.Blue)
        }
    }
}
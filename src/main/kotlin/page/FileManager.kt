package page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bean.FileBean
import dialog.MessageDialog
import tool.ADBUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tool.FileUtil
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File
import javax.swing.DesktopManager

private const val ROOT = "/"
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileManager(deviceId: String,root:Boolean = false) {
    var showDeleteDialog by remember { mutableStateOf<FileBean?>(null) }
    var foldName by remember { mutableStateOf(ROOT) }
    val fileList = remember { mutableStateListOf<FileBean>() }

    // 获取本机文件及文件夹
    LaunchedEffect(foldName,deviceId) {
        withContext(Dispatchers.IO) {
            val list = ADBUtil.fileList(deviceId,foldName,root)
            withContext(Dispatchers.Main) {
                fileList.clear()
                fileList.addAll(list)
                fileList.sortBy { !it.fold }
            }
        }
    }
    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp)
                    .combinedClickable(onClick = {
                        if (foldName != ROOT) {
                            foldName = foldName.removeSuffix("/")
                            foldName = foldName.substring(0,foldName.lastIndexOf('/')+1)
                        }
                    }, onDoubleClick = {
                    }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(foldName != ROOT) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
                Spacer(modifier = Modifier.width(18.dp))
                Text(foldName, fontSize = 16.sp)
            }
        }
        items(fileList.size) {
            val item = fileList[it]
            ContextMenuArea(
                items = {
                    val list = arrayListOf<ContextMenuItem>()
                    if(item.fold){
                    }else{
                        list.add(ContextMenuItem("默认方式打开") { pullFileToCache(deviceId,item,true) })
                    }
                    list.add(ContextMenuItem("导出到电脑") { pullFile(deviceId,item,false) })
                    list.add(ContextMenuItem("删除") { showDeleteDialog = item })
                    list
                }
            ){
                Row(
                    modifier = Modifier
                        .clickable {
                            if (item.fold) {
                                // 去当前文件夹
                                fileList.clear()
                                foldName = "$foldName${item.name}/"
                            }
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(if (item.fold) "ic_folder.svg" else "ic_file.svg"),
                        "文件夹：${item.fold}",
                        modifier = Modifier.width(24.dp).height(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.name, fontSize = 16.sp)
                    if (!item.fold){
                        Text(modifier = Modifier.fillMaxWidth(), text = item.size, fontSize = 16.sp, textAlign = TextAlign.End)
                    }
                }
            }
        }
    }

    if(showDeleteDialog != null) {
        val file = "${showDeleteDialog?.parent}${showDeleteDialog?.name}"
        MessageDialog("注意", "确定要删除${file}吗？", {
            showDeleteDialog = null
        }, {
            ADBUtil.deleteFile(deviceId,file,root)
            fileList.remove(showDeleteDialog)
            showDeleteDialog = null
        })
    }
}

private fun pullFile(deviceId: String,source:FileBean,open:Boolean){
    val fileDialog = FileDialog(ComposeWindow(),"导出文件", FileDialog.SAVE)
    fileDialog.isMultipleMode = false
    fileDialog.isVisible = true
    val directory = fileDialog.directory?.replace("\n","")?.replace("\r","")
    if (directory != null) {
        val path = "$directory${source.name}"
        ADBUtil.exportFile(deviceId,"${source.parent}${source.name}",path)
        if (open){
            FileUtil.openFileWithDefault(File(path))
        }
    }
}

private fun pullFileToCache(deviceId: String,source:FileBean,open:Boolean){
    val cacheDir = FileUtil.getCacheDir()
    val file = File(cacheDir,source.name)
    ADBUtil.exportFile(deviceId,"${source.parent}${source.name}",file.absolutePath)
    if (open){
        FileUtil.openFileWithDefault(file)
    }
}

private fun delete(deviceId: String,source:FileBean){
}
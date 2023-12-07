package bean

abstract class MainNav(val svgName: String, val labelText: String,needDevice:Boolean){
    companion object{
        fun createMainNavData() = mutableListOf(
            CurrentAppInfo(),
            PhoneInfo(),
            QuickFun(),
            Install(),
            FileManage(),
            DeviceRecord(),
            About(),
        )
    }
}
class CurrentAppInfo:MainNav("ic_app_info.svg","应用信息",true){}
class PhoneInfo:MainNav("ic_phone.svg","手机信息",true){}
class QuickFun:MainNav("ic_quick_future.svg","快捷功能",true){}
class FileManage:MainNav("ic_folder.svg","文件管理",true){}
class DeviceRecord:MainNav("ic_phone_record.svg","设备历史",false){}
class Install:MainNav("ic_install.svg","安装应用",false){}
class About:MainNav("ic_other.svg","更多信息",false){}
package bean

data class DeviceInfo(val deviceName: String, val deviceModel: String, val device: String){
    var offline = false
    var ip:String? = null
    var connected = true

    fun isWifiConnect(): Boolean {
        return device == ip
    }
}
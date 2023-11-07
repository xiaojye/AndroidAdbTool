package bean

data class FileBean(val name: String, val fold: Boolean, val size: String){
    var parent:String? = null
}
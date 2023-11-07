package tool

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.platform.Typeface
import org.jetbrains.skia.Data
import org.jetbrains.skia.Typeface
import org.jetbrains.skia.makeFromFile
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
fun ttfFontFamily() = FontFamily(
    Typeface(
        Typeface.makeFromData(Data.makeFromBytes(ResourceLoader.Default.load("font"+File.separator+"iconfont.ttf").readAllBytes())),
    )
)
package net.robmunro.processing.util

import processing.core.PApplet
import java.awt.Color

class ColorUtils {

    companion object {
        val TRANSPARENT = Color(0f, 0f, 0f, 0f)
    }
}

fun Color.toProcessing(p: PApplet) = p.color(red, green, blue, alpha)
fun Color.toProcessing(alpha: Float, p: PApplet) = p.color(red, green, blue, alpha.toInt())
fun String.webc(p: PApplet) = Color.decode(this).toProcessing(p)

fun Color.encodeARGB() = "#${alpha.hex2()}${red.hex2()}${green.hex2()}${blue.hex2()}"
fun String.decodeARGB(): Color = Color(
    substring(3, 5).hex2Int(),
    substring(5, 7).hex2Int(),
    substring(7, 9).hex2Int(),
    substring(1, 3).hex2Int()
)

fun Int.hex2() = this.toString(16).padStart(2, '0')
fun String.hex2Int(): Int = this.toInt(16)
fun red() = Color.RED.encodeARGB()
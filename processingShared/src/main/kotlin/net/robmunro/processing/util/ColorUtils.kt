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

package cubes.util

import processing.core.PApplet

fun PApplet.pushMatrix(body: () -> Unit) {
    this.pushMatrix()
    body()
    this.popMatrix()
}
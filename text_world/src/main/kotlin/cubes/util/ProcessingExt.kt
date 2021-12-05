package cubes.util

import processing.core.PApplet
import processing.core.PVector

fun PApplet.pushMatrix(body: () -> Unit) {
    this.pushMatrix()
    body()
    this.popMatrix()
}

fun PVector.set(i: Int) = set(i.toFloat(), i.toFloat(), i.toFloat())
fun PVector.set(f: Float) = set(f, f, f)
fun PVector.increment(f: Float) = set(this.x + f, this.y + f, this.z + f)
fun PVector.increment(vec: PVector) = set(this.x + vec.x, this.y + vec.y, this.z + vec.z)
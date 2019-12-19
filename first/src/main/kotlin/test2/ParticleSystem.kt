package test2

import processing.core.PApplet
import processing.core.PImage
import processing.core.PShape
import processing.opengl.PShapeOpenGL.createShape
import java.util.*

internal class ParticleSystem(val p: PApplet, n: Int, sprite: PImage) {
    var particles: ArrayList<Particle>
    var particleShape: PShape

    init {
        particles = ArrayList()
        particleShape = p.createShape(PShape.GROUP)
        for (i in 0 until n) {
            val p = Particle(p, sprite)
            particles.add(p)
            particleShape.addChild(p.shape)
        }
    }

    fun update() {
        for (p in particles) {
            p.update()
        }
    }

    fun setEmitter(x: Float, y: Float) {
        for (p in particles) {
            if (p.isDead) {
                p.rebirth(x, y)
            }
        }
    }

    fun display() {
        p.shape(particleShape)
    }
}
package test2

import processing.core.*

internal class Particle(var p: PApplet, sprite: PImage) {
    var velocity: PVector? = null
    var lifespan = 255f
    var shape: PShape
    var partSize: Float
    var gravity = PVector(0f, 0.1f)

    init {
        partSize = p.random(10f, 60f)
        shape = p.createShape()
        shape.beginShape(PConstants.QUAD)
        shape.noStroke()
        shape.texture(sprite)
        shape.normal(0f, 0f, 1f)
        shape.vertex(-partSize / 2, -partSize / 2, 0f, 0f)
        shape.vertex(+partSize / 2, -partSize / 2, sprite.width.toFloat(), 0f)
        shape.vertex(+partSize / 2, +partSize / 2, sprite.width.toFloat(), sprite.height.toFloat())
        shape.vertex(-partSize / 2, +partSize / 2, 0f, sprite.height.toFloat())
        shape.endShape()
        rebirth(p.width / 2f, p.height / 2f)
        lifespan = p.random(255f)
    }

    fun rebirth(x: Float, y: Float) {
        val a = p.random(PConstants.TWO_PI)
        val speed = p.random(0.5f, 4f)
        velocity = PVector(PApplet.cos(a), PApplet.sin(a))
        velocity!!.mult(speed)
        lifespan = 255f
        shape.resetMatrix()
        shape.translate(x, y)
    }

    val isDead: Boolean
        get() = lifespan < 0

    fun update() {
        lifespan = lifespan - 1
        velocity!!.add(gravity)
        shape.setTint(p.color(255f, lifespan))
        shape.translate(velocity!!.x, velocity!!.y)
    }
}
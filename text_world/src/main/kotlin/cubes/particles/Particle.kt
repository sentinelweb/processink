package cubes.particles

import cubes.models.Shape
import cubes.util.increment
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

class Particle(var p: PApplet, sprite: Shape) {

    var velocity: PVector = PVector(.1f, .2f, 0f)
    var lifespan = 255f
    var shape: Shape
    //var partSize: Float
    //var gravity = PVector(0f, 0.1f)

    init {
        //partSize = p.random(10f, 60f)
        shape = sprite
        //rebirth(p.width / 2f, p.height / 2f)
        lifespan = p.random(255f)
    }

    private fun updatePosition() {
        val a = p.random(PConstants.TWO_PI)
        val speed = p.random(0.5f, 4f)
//        velocity = PVector(PApplet.cos(a), PApplet.sin(a))
//        velocity!!.mult(speed)
        //lifespan = 255f
        shape.position.increment(velocity.copy().mult(speed))
    }

    val isDead: Boolean
        get() = lifespan < 0

    fun update() {
        lifespan = lifespan - 1
        //velocity!!.add(gravity)
        updatePosition()
    }

    fun draw() {
        println("Particle:draw - ${shape.position}")
        shape.draw()
    }
}
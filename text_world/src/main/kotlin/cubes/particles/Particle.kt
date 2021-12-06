package cubes.particles

import cubes.models.Shape
import cubes.util.increment
import net.robmunro.processing.util.alpha
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

class Particle(var p: PApplet, sprite: Shape, val useGravity: Boolean = false) {

    private var velocity: PVector
    private var lifespan = LIFESPAN
    private var shape: Shape
    private val gravity = PVector(0f, 0.1f)

    private var originalFillAlpha = 255;

    init {
        shape = sprite
        lifespan = p.random(205f).toInt() + 50
        velocity =
            PVector(p.random(MAX_SPEED), p.random(MAX_SPEED), p.random(MAX_SPEED))
                .sub(MAX_SPEED / 2, MAX_SPEED / 2, MAX_SPEED / 2)
    }

    private fun updateParticle() {
        val a = p.random(PConstants.TWO_PI)
        if (useGravity) {
            velocity.add(gravity)
        }
        shape.position.increment(velocity)
    }

    val isDead: Boolean
        get() = lifespan < 0

    fun update() {
        lifespan = lifespan - 1
        updateParticle()
    }

    fun draw() {
        if (lifespan == FADEOUT) {
            originalFillAlpha = shape.fillColor.alpha
        } else if (lifespan < FADEOUT && lifespan >= 0) {
            shape.fillColor = shape.fillColor.alpha(originalFillAlpha * (lifespan / FADEOUT.toFloat()))
            if (shape.stroke) {
                shape.strokeColor = shape.strokeColor.alpha(255 * (lifespan / FADEOUT.toFloat()))
            }
        }
        shape.draw()
    }

    companion object {
        private const val MAX_SPEED = 10f
        private const val LIFESPAN = 255
        private const val FADEOUT = 50
    }
}
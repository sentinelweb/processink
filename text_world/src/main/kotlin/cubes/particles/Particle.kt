package cubes.particles

import cubes.models.Shape
import cubes.util.increment
import net.robmunro.processing.util.alpha
import processing.core.PApplet
import processing.core.PVector
import provider.TimeProvider

class Particle(
    var p: PApplet,
    sprite: Shape,
    maxLifeSpan: Int,
    val useGravity: Boolean = false,
    val timeProvider: TimeProvider = TimeProvider()
) {

    private var velocity: PVector
    private var lifespan: Int
    private var shape: Shape
    private val gravity = PVector(0f, 0.1f)
    private val start: Long
    private val fadeOutTime = 300f
    private var originalFillAlpha = 255

    private val time: Long
        get() = timeProvider.getTime() - start

    private val timeLeft: Long
        get() = lifespan - time

    init {
        shape = sprite
        lifespan = p.random(maxLifeSpan - fadeOutTime).toInt() + fadeOutTime.toInt()
        velocity =
            PVector(p.random(MAX_SPEED), p.random(MAX_SPEED), p.random(MAX_SPEED))
                .sub(MAX_SPEED / 2, MAX_SPEED / 2, MAX_SPEED / 2)
        start = timeProvider.getTime()
        originalFillAlpha = shape.fillColor.alpha
    }

    private fun updateParticle() {
        if (useGravity) {
            velocity.add(gravity)
        }
        shape.position.increment(velocity)
    }

    val isDead: Boolean
        get() = timeLeft < 0

    fun update() {
        updateParticle()
    }

    fun draw() {
        if (timeLeft < fadeOutTime && timeLeft >= 0) {
            shape.fillColor = shape.fillColor.alpha(originalFillAlpha * (timeLeft / fadeOutTime))
            if (shape.stroke) {
                shape.strokeColor = shape.strokeColor.alpha(255 * (timeLeft / fadeOutTime))
            }
        }
        shape.draw()
    }

    companion object {
        private const val MAX_SPEED = 10f
    }
}
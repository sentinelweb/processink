package cubes.particles

import cubes.models.Shape
import cubes.util.pushMatrix
import processing.core.PApplet

class ParticleSystem(
    override var p: PApplet? = null,
    n: Int,
    private val shapeProvider: (Int) -> Shape
) : Shape(p) {

    private lateinit var particles: ArrayList<Particle>

    init {
        init(n)
    }

    private fun init(n: Int) {
        particles = ArrayList()
        for (i in 0 until n) {
            val particle = Particle(p!!, shapeProvider.invoke(i))
            particles.add(particle)
        }
    }

    fun update() {
        for (particle in particles) {
            particle.update()
        }
    }

    fun isDead(): Boolean = particles.all { it.isDead }

    override fun draw() {
        p?.apply {
            pushMatrix {
                translate(position.x, position.y, position.z)
                pushMatrix {
                    for (particle in particles) {
                        particle.draw()
                    }
                }
            }
        }
    }

}
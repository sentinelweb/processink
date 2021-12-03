package cubes.models

import cubes.motion.Motion
import cubes.motion.VelocityRotationMotion
import cubes.util.pushMatrix
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet

@Serializable
class CubeList constructor(
    @Transient
    override var p: PApplet? = null,
    val length: Int,
    val startSize: Float,
    val endSisze: Float
) : Shape(p) {

    @Contextual
    var cubes: List<Cube> = listOf()
    val increment = (endSisze - startSize) / length

    @Transient
    var cubeListMotion: Motion<Cube, Any> = DEFAULT_MOTION_UPDATER

    init {
        intialise(length)
    }

    private fun intialise(length: Int) {
        if (cubes.isEmpty()) {
            cubes = (0..length - 1).map {
                Cube(p, 1f).apply {
                    val scaleRatio = startSize + (it * increment)
                    scale.set(scaleRatio, scaleRatio, scaleRatio)
                }
            }
        }
    }

    override fun updateState() {
        motion.execute(0, this)
        cubes.forEachIndexed { i, cube ->
            cubeListMotion.execute(i, cube)
        }
    }

    override fun draw() {
        if (visible) {
            p?.apply {
                pushMatrix {
                    translate(width / 2f, height / 2f)
                    cubes.forEachIndexed { i, cube ->
                        cube.draw()
                    }
                }
                noStroke()
            }
        }
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        p = applet
        cubes.forEach { it.setApplet(applet) }
    }

    companion object {
        val DEFAULT_MOTION_UPDATER = VelocityRotationMotion(0.001f, 0.01f)
    }
}
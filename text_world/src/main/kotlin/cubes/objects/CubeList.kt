package cubes.objects

import cubes.motion.Motion
import cubes.motion.VelocityRotationMotion
import cubes.util.pushMatrix
import processing.core.PApplet

class CubeList constructor(
    private val p: PApplet,
    length: Int,
    private val startSize: Float,
    endSisze: Float
) : Shape(p) {

    val cubes: List<Cube>
    val increment = (endSisze - startSize) / length
    var cubeListMotion: Motion<Cube, Any> = DEFAULT_MOTION_UPDATER

    init {
        cubes = (0..length - 1).map {
            Cube(p, 1f).apply {
                val scaleRatio = startSize + (it * increment)
                scale.set(scaleRatio, scaleRatio, scaleRatio)
            }
        }
    }

    fun updateState() {
        cubes.forEachIndexed { i, cube ->
            cubeListMotion.execute(i, cube)
        }
    }

    fun draw(contextFunction: ((i: Int, cube: Cube) -> Unit)? = null) {
        if (visible) {
            p.pushMatrix {
                p.translate(p.width / 2f, p.height / 2f)
                cubes.forEachIndexed { i, cube ->
                    contextFunction?.let { it(i, cube) }
                    cube.draw()
                }
            }
        }
    }

    companion object {
        val DEFAULT_MOTION_UPDATER = VelocityRotationMotion(0.001f, 0.01f)
    }
}
package cubes

import cubes.motion.Motion
import cubes.motion.VelocityRotationMotion
import processing.core.PApplet

class CubeList constructor(
    private val p: PApplet,
    length: Int,
    private val startSize: Float,
    endSisze: Float
) {
    val cubes: MutableList<Cube> = mutableListOf()

    val increment = (endSisze - startSize) / length
    var stateUpdater: MotionUpdater = DEFAULT_MOTION_UPDATER

    init {
        (0..length - 1).forEach {
            cubes.add(Cube(p, startSize + (it * increment)))
        }
    }

    fun updateState() {
        cubes.forEachIndexed { i, cube ->
            if (!stateUpdater.isEnded()) {
                stateUpdater.updateState(i, cube)
            } else {
                stateUpdater.callEndOnce()
            }
        }
    }

    // make a algo to send different cubes to catch each other up.
    fun draw(contextFunction: ((i: Int, cube: Cube) -> Unit)? = null) {
        p.pushMatrix()
        p.translate(p.width / 2f, p.height / 2f)
        cubes.forEachIndexed { i, cube ->
            contextFunction?.let { it(i, cube) }
            cube.draw()
        }
        p.popMatrix()
    }

    abstract class MotionUpdater constructor(endFunction: () -> Unit = {}): Motion(endFunction) {
        abstract fun updateState(i: Int, cube: Cube)
    }

    companion object {
        val DEFAULT_MOTION_UPDATER = VelocityRotationMotion(0.001f)
    }
}
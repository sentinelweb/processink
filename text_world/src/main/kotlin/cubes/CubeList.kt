package cubes

import processing.core.PApplet

class CubeList constructor(
    private val p: PApplet,
    length: Int,
    private val startSize: Float,
    endSisze: Float
) {
    val cubes: MutableList<Cube> = mutableListOf()

    val increment = (endSisze - startSize) / length
    var stateUpdater: ((i: Int, cube: Cube) -> Unit) = DEFAULT_MOTION_UPDATER

    init {
        stateUpdater = DEFAULT_MOTION_UPDATER
        (0..length - 1).forEach {
            cubes.add(Cube(p, startSize + (it * increment)))
        }
    }

    // make a algo to send different cubes to catch each other up.
    fun draw(contextFunction: ((i: Int, cube: Cube) -> Unit)? = null) {
        p.pushMatrix()
        p.translate(p.width / 2f, p.height / 2f)
        cubes.forEachIndexed { i, cube ->
            stateUpdater(i, cube)
            contextFunction?.let { it(i, cube) }
            cube.draw()
        }
        p.popMatrix()
    }

    companion object {
        val DEFAULT_MOTION_UPDATER = fun(i: Int, cube: Cube) {
            val fl = 0.001f * (i + 1)
            cube.angle = Triple(
                cube.angle.first + fl,
                cube.angle.second + fl,
                cube.angle.third + fl
            )
        }
    }
}
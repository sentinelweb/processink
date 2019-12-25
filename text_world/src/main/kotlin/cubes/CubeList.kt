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

    val motionUpdater = fun(i: Int, cube: Cube) {
        cube.angle += 0.001f * (i+1)
        // cube.angle += 0.001f + if (i>0) cubes[i-1].angle * 0.01f else 0f // if  .. 1f wow neat - it must mean something
    }

    init {
        (0..length - 1).forEach {
            cubes.add(Cube(p, startSize + (it * increment)))
        }
    }

    // make a algo to send different cubes to catch each other up.
    fun draw(fn: ((i: Int, cube: Cube) -> Unit)) {
        p.pushMatrix()
        p.translate(p.width / 2f, p.height / 2f)
        cubes.forEachIndexed { i, cube ->
            motionUpdater(i, cube)
            fn(i, cube)
            cube.draw()
        }
        p.popMatrix()
    }
}
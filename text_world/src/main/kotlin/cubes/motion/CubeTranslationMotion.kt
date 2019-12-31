package cubes.motion

import cubes.Cube
import cubes.CubeList
import processing.core.PVector
import provider.TimeProvider

class CubeTranslationMotion constructor(
    private val cubeList: CubeList,
    private val timeMs: Float,
    private val target: List<PVector> = cubeList.cubes.map { PVector() },
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<Cube>(endFunction) {

    private val start = cubeList.cubes.map { cube -> cube.position.copy() }
    private val startTime = timeProvider.getTime()

    override fun updateState(i: Int, shape: Cube) {
        if (isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.position.set(
            interpolate(start[i].x, target[i].x, ratio),
            interpolate(start[i].y, target[i].y, ratio),
            interpolate(start[i].z, target[i].z, ratio)
        )
    }

    override fun isEnded() = timeProvider.getTime() - startTime > timeMs

    override fun ensureEndState() {
        cubeList.cubes.forEachIndexed { i, cube ->
            cube.scale.set(target[i])
        }
    }

    companion object {

        fun grid(cubeList: CubeList, timeMs: Float, dimension: Int, spacing: Float, endFunction: () -> Unit = {}) =
            CubeTranslationMotion(
                cubeList,
                timeMs,
                cubeList.cubes.mapIndexed { i, cube ->
                    val row = i % dimension
                    val col = i / dimension
                    PVector(
                        -spacing / 2 + (row - 1) * spacing,//TODO assuming 4 dimension sub len/2 somehow
                        -spacing / 2 + (col - 1) * spacing,
                        0f
                    )
                },
                endFunction = endFunction
            )

        fun line(cubeList: CubeList, timeMs: Float, spacing: Float, endFunction: () -> Unit = {}) =
            CubeTranslationMotion(
                cubeList,
                timeMs,
                cubeList.cubes.mapIndexed { i, cube ->
                    PVector(
                        -spacing / 2 + (i - cubeList.cubes.size / 2 - 1) * spacing,
                        0f,
                        0f
                    )
                },
                endFunction = endFunction
            )


    }
}
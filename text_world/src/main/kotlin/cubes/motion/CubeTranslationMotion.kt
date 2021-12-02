package cubes.motion


import cubes.motion.interpolator.Interpolator
import cubes.objects.Cube
import cubes.objects.CubeList
import processing.core.PVector
import provider.TimeProvider

class CubeTranslationMotion constructor(
    private val cubeList: CubeList,
    timeMs: Float,
    target: List<PVector> = cubeList.cubes.map { PVector() },
    timeProvider: TimeProvider = TimeProvider(),
    interp: Interpolator? = null,
    endFunction: () -> Unit = {}
) : TranslationMotion<Cube>(timeMs, target, timeProvider, interp, endFunction) {

    override fun getStartData() = cubeList.cubes.map { cube -> cube.position.copy() }

    override fun ensureEndState() {
        cubeList.cubes.forEachIndexed { i, cube ->
            cube.position.set(target[i])
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
                        -spacing / 2f + (row - 1) * spacing / (dimension / 2f),
                        -spacing / 2f + (col - 1) * spacing / (dimension / 2f),
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
                        -spacing / 2 + i * spacing / cubeList.cubes.size,
                        0f,
                        0f
                    )
                },
                endFunction = endFunction
            )

        fun zero(cubeList: CubeList, timeMs: Float, endFunction: () -> Unit = {}) =
            CubeTranslationMotion(
                cubeList,
                timeMs,
                cubeList.cubes.mapIndexed { i, cube -> PVector(0f, 0f, 0f) },
                endFunction = endFunction
            )

    }
}
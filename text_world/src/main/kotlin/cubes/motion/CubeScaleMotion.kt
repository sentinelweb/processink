package cubes.motion

import cubes.objects.Cube
import cubes.objects.CubeList
import cubes.objects.Shape
import processing.core.PVector
import provider.TimeProvider

class CubeScaleMotion constructor(
    private val cubeList: CubeList,
    timeMs: Float,
    target: List<PVector> = cubeList.cubes.map { PVector() },
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : ScaleMotion<Cube>(timeMs, target, timeProvider, endFunction) {

    override fun getStartData(): List<PVector> {
        return cubeList.cubes.map { cube -> cube.scale.copy() }
    }

    override fun ensureEndState() {
        cubeList.cubes.forEachIndexed { i, cube ->
            cube.scale.set(target[i])
        }
    }

    companion object {

        fun scale(cubeList: CubeList, timeMs: Float, scale: Float, endFunction: () -> Unit = {}) =
            CubeScaleMotion(
                cubeList,
                timeMs,
                cubeList.cubes.map {
                    PVector(scale, scale, scale)
                },
                endFunction = endFunction
            )

        fun range(cubeList: CubeList, timeMs: Float, cubeScale: Float, cubeScaleDist: Float, endFunction: () -> Unit = {}) =
            CubeScaleMotion (
                cubeList,
                timeMs,
                cubeList.cubes.mapIndexed{i,cube ->
                    val increment = (cubeScaleDist) / cubeList.cubes.size
                    val scale = cubeScale + (i * increment)
                    PVector(scale, scale, scale)
                },
                endFunction = endFunction
            )

    }

}
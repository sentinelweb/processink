package cubes.motion

import cubes.objects.Cube
import cubes.objects.CubeList
import processing.core.PVector
import provider.TimeProvider

class CubeScaleMotion constructor(
    private val cubeList: CubeList,
    private val timeMs: Float,
    private val target: List<PVector> = cubeList.cubes.map { PVector() },
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<Cube>(endFunction) {

    private val start = cubeList.cubes.map { cube -> cube.scale.copy() }
    private val startTime = timeProvider.getTime()

    override fun updateState(i: Int, shape: Cube) {
        if (isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.scale.set(
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
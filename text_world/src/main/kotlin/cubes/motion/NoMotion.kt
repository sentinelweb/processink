package cubes.motion

import cubes.motion.interpolator.Interpolator
import cubes.objects.Shape
import processing.core.PVector
import provider.TimeProvider

class NoMotion constructor(
    timeProvider: TimeProvider = TimeProvider(),
    interp: Interpolator? = null,
    endFunction: () -> Unit = {}
) : Motion<Shape, PVector>(timeProvider, interp, endFunction) {

    override fun <T : Shape> updateState(i: Int, shape: T) = Unit

    override fun isEnded(): Boolean = true

    override fun getStartData(): List<PVector> = listOf()

    override fun ensureEndState() {}
}

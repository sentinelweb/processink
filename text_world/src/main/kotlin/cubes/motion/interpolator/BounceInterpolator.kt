package cubes.motion.interpolator

class BounceInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun out(t: Float): Float {
        var t = t
        return if (t < 1 / 2.75) {
            7.5625f * t * t
        } else if (t < 2 / 2.75) {
            t -= (1.5f / 2.75f)
            7.5625f * t * t + .75f
        } else if (t < 2.5 / 2.75) {
            t -= (2.25f / 2.75f)
            7.5625f * t * t + .9375f
        } else {
            t -= (2.625f / 2.75f)
            7.5625f * t * t + .984375f
        }
    }

    private fun `in`(t: Float): Float {
        return 1 - out(1 - t)
    }

    private fun inout(t: Float): Float {
        return if (t < 0.5f) {
            `in`(t * 2) * .5f
        } else {
            out(t * 2 - 1) * .5f + .5f
        }
    }

}
package cubes.motion.interpolator


class QuadInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun `in`(t: Float): Float {
        return t * t
    }

    private fun out(t: Float): Float {
        return -t * (t - 2)
    }

    private fun inout(t: Float): Float {
        var t = t
        t *= 2f
        return if (t < 1) {
            0.5f * t * t
        } else {
            -0.5f * (--t * (t - 2) - 1)
        }
    }

}
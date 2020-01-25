package cubes.motion.interpolator

class BackInterpolator(
    private val type: EasingType,
    private val overshot: Float
) : Interpolator {
    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t, overshot)
            EasingType.OUT -> out(t, overshot)
            EasingType.INOUT -> inout(t, overshot)
        }
    }

    private fun `in`(t: Float, o: Float): Float {
        var o = o
        if (o == 0f) {
            o = 1.70158f
        }
        return t * t * ((o + 1) * t - o)
    }

    private fun out(t: Float, o: Float): Float {
        var t = t
        var o = o
        if (o == 0f) {
            o = 1.70158f
        }
        return 1.let { t -= it; t } * t * ((o + 1) * t + o) + 1
    }

    private fun inout(t: Float, o: Float): Float {
        var t = t
        var o = o
        if (o == 0f) {
            o = 1.70158f
        }
        t *= 2f
        o *= 1.525f
        return if (t < 1) {
            0.5f * (t * t * (o + 1) * t - o)
        } else {
            t -= 2
            0.5f * (t * t * (o + 1) * t + o) + 2
        }
    }
}
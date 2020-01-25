package cubes.motion.interpolator

class ExpoInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun `in`(t: Float): Float {
        return if (t == 0f) 0f else Math.pow(2.0, 10 * (t - 1).toDouble()).toFloat()
    }

    private fun out(t: Float): Float {
        return if (t >= 1) 1f else (-Math.pow(2.0, -10 * t.toDouble()) + 1f).toFloat()
    }

    private fun inout(t: Float): Float {
        var t = t
        if (t == 0f) {
            return 0f
        }
        if (t >= 1) {
            return 1f
        }
        t *= 2f
        return if (t < 1) {
            (0.5f * Math.pow(2.0, 10 * (t - 1).toDouble())).toFloat()
        } else {
            (0.5f * (-Math.pow(2.0, -10f * (t - 1).toDouble()) + 2)).toFloat()
        }
    }

}
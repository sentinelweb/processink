package cubes.motion.interpolator

class CircInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun `in`(t: Float): Float {
        return (-(Math.sqrt(1 - t * t.toDouble()) - 1)).toFloat()
    }

    private fun out(t: Float): Float {
        var t = t
        return Math.sqrt(1 - 1.let { t -= it; t } * t.toDouble()).toFloat()
    }

    private fun inout(t: Float): Float {
        var t = t
        t *= 2f
        return if (t < 1) {
            (-0.5f * (Math.sqrt(1 - t * t.toDouble()) - 1)).toFloat()
        } else {
            (0.5f * (Math.sqrt(1 - 2.let { t -= it; t } * t.toDouble()) + 1)).toFloat()
        }
    }

}
package cubes.motion.interpolator

class QuintInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun `in`(t: Float): Float {
        return t * t * t * t * t
    }

    private fun out(t: Float): Float {
        var t = t
        return 1.let { t -= it; t } * t * t * t * t + 1
    }

    private fun inout(t: Float): Float {
        var t = t
        t *= 2f
        return if (t < 1) {
            0.5f * t * t * t * t * t
        } else {
            0.5f * (2.let { t -= it; t } * t * t * t * t + 2)
        }
    }

}
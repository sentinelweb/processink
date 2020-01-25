package cubes.motion.interpolator

class SineInterpolator(private val type: EasingType) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t)
            EasingType.OUT -> out(t)
            EasingType.INOUT -> inout(t)
        }
    }

    private fun `in`(t: Float): Float {
        return (-Math.cos(t * (Math.PI / 2)) + 1).toFloat()
    }

    private fun out(t: Float): Float {
        return Math.sin(t * (Math.PI / 2)).toFloat()
    }

    private fun inout(t: Float): Float {
        return (-0.5f * (Math.cos(Math.PI * t) - 1)).toFloat()
    }

}
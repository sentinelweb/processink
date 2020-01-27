package cubes.motion.interpolator

class ElasticInterpolator(
    private val type: EasingType,
    private val amplitude: Float,
    private val period: Float
) : Interpolator {

    override fun getInterpolation(t: Float): Float {
        return when (type) {
            EasingType.IN -> `in`(t, amplitude, period)
            EasingType.OUT -> out(t, amplitude, period)
            EasingType.INOUT -> inout(t, amplitude, period)
        }
    }

    private fun `in`(t: Float, a: Float, p: Float): Float {
        var t = t
        var a = a
        var p = p
        if (t == 0f) {
            return 0f
        }
        if (t >= 1) {
            return 1f
        }
        if (p == 0f) {
            p = 0.3f
        }
        val s: Float
        if (a == 0f || a < 1) {
            a = 1f
            s = p / 4
        } else {
            s = (p / (2 * Math.PI) * Math.asin(1 / a.toDouble())).toFloat()
        }
        return (-(a * Math.pow(
            2.0,
            10 * 1.let { t -= it; t }.toDouble()
        ) * Math.sin((t - s) * (2 * Math.PI) / p))).toFloat()
    }

    private fun out(t: Float, a: Float, p: Float): Float {
        var a = a
        var p = p
        if (t == 0f) {
            return 0f
        }
        if (t >= 1) {
            return 1f
        }
        if (p == 0f) {
            p = 0.3f
        }
        val s: Float
        if (a == 0f || a < 1) {
            a = 1f
            s = p / 4
        } else {
            s = (p / (2 * Math.PI) * Math.asin(1 / a.toDouble())).toFloat()
        }
        return (a * Math.pow(
            2.0,
            -10 * t.toDouble()
        ) * Math.sin((t - s) * (2 * Math.PI) / p) + 1).toFloat()
    }

    private fun inout(t: Float, a: Float, p: Float): Float {
        var t = t
        var a = a
        var p = p
        if (t == 0f) {
            return 0f
        }
        if (t >= 1) {
            return 1f
        }
        if (p == 0f) {
            p = .3f * 1.5f
        }
        val s: Float
        if (a == 0f || a < 1) {
            a = 1f
            s = p / 4
        } else {
            s = (p / (2 * Math.PI) * Math.asin(1 / a.toDouble())).toFloat()
        }
        t *= 2f
        return if (t < 1) {
            (-.5 * (a * Math.pow(
                2.0,
                10 * 1.let { t -= it; t }.toDouble()
            ) * Math.sin((t - s) * (2 * Math.PI) / p))).toFloat()
        } else {
            (a * Math.pow(
                2.0,
                -10 * 1.let { t -= it; t }.toDouble()
            ) * Math.sin((t - s) * (2 * Math.PI) / p) * .5 + 1).toFloat()
        }
    }

}
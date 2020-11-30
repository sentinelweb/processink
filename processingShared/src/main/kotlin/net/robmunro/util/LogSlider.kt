package net.robmunro.util

import javax.swing.JSlider
import kotlin.math.exp
import kotlin.math.ln

class LogSlider constructor(
    val minValue: Float = 100f,
    val maxValue: Float = 10000000.0f,
    private val minPosition: Int = 0,
    private val maxPosition: Int = 100
) {
    constructor(slider: JSlider, minValue: Float = 100f, maxValue: Float = 10000000.0f) : this(
        minValue,
        maxValue,
        slider.minimum,
        slider.maximum
    )

    val scale: Float = (ln(maxValue) - ln(minValue)) / (maxPosition - minPosition)

    fun toValueFromSlider(slider: Int): Float = toValueFromSlider(slider.toFloat())

    fun toValueFromSlider(silder: Float): Float {
        return exp(ln(minValue) + scale * (silder - minPosition))
    }

    fun toSliderFromValue(value: Float): Int {
        return ((ln(value) - ln(minValue)) / scale + minPosition).toInt()
    }

    fun toSliderFromValueF(value: Float): Float {
        return ((ln(value) - ln(minValue)) / scale + minPosition).toFloat()
    }
}
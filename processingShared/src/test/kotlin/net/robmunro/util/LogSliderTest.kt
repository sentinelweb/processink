package net.robmunro.util

import com.flextrade.jfixture.JFixture
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.junit.Assert.assertEquals
import org.junit.Test

class LogSliderTest {

    val sut = LogSlider(10f, 10000f, 0, 100)
    val fixture = JFixture()

    @Test
    fun logSlider() {
        val sliderVal: Float = 50f //fixture.createValue()
        val output = sut.toValueFromSlider(sliderVal)
        assertEquals(sliderVal, sut.toSliderFromValueF(output))
    }

    @Test
    fun logPosition() {
        val value: Float = 500f //fixture.createValue()
        val output = sut.toSliderFromValueF(value)
        MatcherAssert.assertThat(
            sut.toValueFromSlider(output).toDouble(),
            Is.`is`(Matchers.closeTo(value.toDouble(), 0.001))
        )
    }

    inline fun <reified T> JFixture.createValue(): T =
        this.create(T::class.java)

}
package cubes.motion.interpolator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SineInterpolatorTest {

    private lateinit var sut: SineInterpolator

    @Before
    fun setUp() {
    }

    @Test
    fun getInterpolation_in() {

        sut = SineInterpolator(EasingType.IN)

        assertEquals(0f, sut.getInterpolation(0f))
        assertEquals(1f, sut.getInterpolation(1f))
    }
}
package cubes.motion

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import cubes.objects.CubeList
import org.junit.Before
import org.junit.Test
import cubes.CubesProcessingView
import cubes.motion.Motion.Companion.interpolate
import org.hamcrest.Matchers.closeTo
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import processing.core.PConstants
import processing.core.PShape
import processing.core.PVector
import provider.TimeProvider

class CubeRotationAlignMotionTest {
    @Mock
    lateinit var mockApplet: CubesProcessingView
    @Mock
    lateinit var mockPShape: PShape
    @Mock
    lateinit var mockTimeProvider: TimeProvider

    private lateinit var sut: CubeRotationAlignMotion

    private lateinit var fixtCubeList: CubeList

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(mockApplet.createShape(PConstants.BOX, 20f, 20f, 20f)).thenReturn(mockPShape)
        whenever(mockTimeProvider.getTime()).thenReturn(0)
        fixtCubeList = generateCubes(1)
    }

    @Test
    fun isEnded() {
        whenever(mockTimeProvider.getTime()).thenReturn(0, (FIXT_TIME / 2), FIXT_TIME-1, FIXT_TIME, FIXT_TIME+1)
        sut = createSut()
        assertFalse(sut.isEnded())
        assertFalse(sut.isEnded())
        assertFalse(sut.isEnded())
        assertTrue(sut.isEnded())
    }

    @Test
    fun updateState() {
        whenever(mockTimeProvider.getTime()).thenReturn(0, 0, (FIXT_TIME / 2), FIXT_TIME)
        val initial = PVector(0f, PI, 3f * PI / 2)
        fixtCubeList.cubes[0].angle = initial
        sut = createSut()
        // time ratio 0
        sut.updateState(0, fixtCubeList.cubes[0])
        assertEquals(initial, fixtCubeList.cubes[0].angle)
        // time ratio 0.5
        sut.updateState(0, fixtCubeList.cubes[0])
        assertEquals(
            PVector(initial.x, initial.y * 0.5f,initial.z * 0.5f),
            fixtCubeList.cubes[0].angle
        )
        // time ratio 1
        sut.updateState(0, fixtCubeList.cubes[0])
        assertEquals(PVector(0f, 0f, 0f), fixtCubeList.cubes[0].angle)
    }

    @Test
    fun interpolate() {// takinf 6s to run? why?
        sut = createSut()
        assertEquals(0.0f, interpolate(sut.wrapTo2Pi(20f), 0f, FIXT_RATIO_END))
        assertEquals(2 * PI, interpolate(2 * PI, 0f, FIXT_RATIO_START))
        assertEquals(PI, interpolate(2 * PI, 0f, 0.5f))
        assertThat(interpolate(2 * PI, 0f, 0.75f).toDouble(), `is`(closeTo((PI / 2).toDouble(), 0.0001)))
        assertThat(interpolate(2 * PI, 0f, 0.25f).toDouble(), `is`(closeTo((3 * PI / 2).toDouble(), 0.0001)))
    }

    @Test
    fun wrap2Pi_float() {
        sut = createSut()
        assertThat(sut.wrapTo2Pi(TWO_PI).toDouble(), `is`(closeTo(0.0, 0.0001)))
        assertThat(sut.wrapTo2Pi(PI).toDouble(), `is`(closeTo(PI.toDouble(), 0.0001)))
        assertThat(sut.wrapTo2Pi(11 * PI / 4).toDouble(), `is`(closeTo((3 * PI / 4).toDouble(), 0.0001)))
        assertThat(sut.wrapTo2Pi(-11 * PI / 4).toDouble(), `is`(closeTo((5 * PI / 4).toDouble(), 0.0001)))
    }

    private fun createSut() =
        CubeRotationAlignMotion(fixtCubeList, FIXT_TIME.toFloat(), timeProvider = mockTimeProvider)

    private fun generateCubes(length: Int):CubeList {
        val shape:PShape = mock()
        whenever(mockApplet.createShape(PConstants.BOX, 1f, 1f, 1f)).thenReturn(shape)
        return CubeList(mockApplet, length, FIXT_SIZE, FIXT_SIZE + (length - 1f * FIXT_SIZE))
    }

    companion object {
        const val FIXT_SIZE = 20f
        const val FIXT_TIME = 1000L
        const val FIXT_RATIO_START = 0f
        const val FIXT_RATIO_END = 1f

        const val TWO_PI = (Math.PI * 2f).toFloat()
        const val PI = Math.PI.toFloat()
    }
}
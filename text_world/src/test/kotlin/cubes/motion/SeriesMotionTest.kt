package cubes.motion

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import cubes.CubesProcessingView
import cubes.objects.Shape
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import provider.TimeProvider

class SeriesMotionTest {
    val mockApplet: CubesProcessingView = mock()
    val mockTimeProvider: TimeProvider = mock()

    private lateinit var fixtMotion1: TestMotion
    private lateinit var fixtMotion2: TestMotion
    private lateinit var fixtMotion3: TestMotion
    private lateinit var fixtMotion4: TestMotion
    private lateinit var fixtShape: Shape

    private lateinit var sut: SeriesMotion<Shape>

    private val end1 = mock<(() -> Unit)>()
    private val end2 = mock<(() -> Unit)>()
    private val end3 = mock<(() -> Unit)>()
    private val end4 = mock<(() -> Unit)>()
    private val endSeries = mock<(() -> Unit)>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this::class.java)
        fixtMotion1 = TestMotion(FIXT_TIME, mockTimeProvider, end1)
        fixtMotion2 = TestMotion(FIXT_TIME, mockTimeProvider, end2)
        fixtMotion3 = TestMotion(FIXT_TIME, mockTimeProvider, end3)
        fixtMotion4 = TestMotion(FIXT_TIME, mockTimeProvider, end4)
        fixtShape = object : Shape(mockApplet) {}
    }

    @Test
    fun `executes motions in series`() {
        sut = SeriesMotion(listOf(fixtMotion1, fixtMotion2, fixtMotion3), mockTimeProvider, endSeries)
        whenever(mockTimeProvider.getTime()).thenReturn(0)
        sut.start()

        assertTrue(fixtMotion1.isStarted())
        whenever(mockTimeProvider.getTime()).thenReturn((FIXT_TIME).toLong())
        sut.updateState(0, fixtShape)
        assertTrue(fixtMotion1.isEnded())
        assertTrue(fixtMotion2.isStarted())
        assertFalse(fixtMotion2.isEnded())

        whenever(mockTimeProvider.getTime()).thenReturn((FIXT_TIME * 2).toLong())
        sut.updateState(0, fixtShape)
        assertTrue(fixtMotion2.isEnded())
        assertTrue(fixtMotion3.isStarted())
        assertFalse(fixtMotion3.isEnded())

        whenever(mockTimeProvider.getTime()).thenReturn((FIXT_TIME * 3).toLong())
        sut.updateState(0, fixtShape)
        assertTrue(fixtMotion1.isEnded())
        assertTrue(fixtMotion2.isEnded())
        assertTrue(fixtMotion3.isEnded())
        assertTrue(sut.isEnded())

        verify(end1)()
        verify(end2)()
        verify(end3)()
        verify(endSeries)()
    }

    @Test
    fun `executes motions in series with sub-parallel`() {
        val endComp1 = mock<(() -> Unit)>()
        val endComp2 = mock<(() -> Unit)>()

        sut = SeriesMotion(
            listOf(
                CompositeMotion(listOf(fixtMotion1, fixtMotion2), mockTimeProvider, endComp1),
                CompositeMotion(listOf(fixtMotion3, fixtMotion4), mockTimeProvider, endComp2)
            ),
            mockTimeProvider,
            endSeries
        )

        whenever(mockTimeProvider.getTime()).thenReturn(0)
        sut.start()

        assertTrue(fixtMotion1.isStarted())
        assertTrue(fixtMotion2.isStarted())
        assertFalse(fixtMotion1.isEnded())
        assertFalse(fixtMotion2.isEnded())
        assertFalse(fixtMotion3.isStarted())
        assertFalse(fixtMotion4.isStarted())

        whenever(mockTimeProvider.getTime()).thenReturn((FIXT_TIME).toLong())
        sut.updateState(0, fixtShape)
        assertTrue(fixtMotion1.isEnded())
        assertTrue(fixtMotion2.isEnded())
        assertTrue(fixtMotion3.isStarted())
        assertTrue(fixtMotion4.isStarted())
        assertFalse(fixtMotion3.isEnded())
        assertFalse(fixtMotion4.isEnded())

        verify(end1)()
        verify(end2)()
        verify(endComp1)()
        verify(end3, never())()
        verify(end4, never())()
        verify(endComp2, never())()
        verify(endSeries, never())()

        whenever(mockTimeProvider.getTime()).thenReturn((FIXT_TIME * 2).toLong())
        sut.updateState(0, fixtShape)
        assertTrue(fixtMotion3.isEnded())
        assertTrue(fixtMotion4.isEnded())
        verify(end3)()
        verify(end4)()
        verify(endComp1)()
        verify(endComp2)()
        verify(endSeries)()
    }

    companion object {
        const val FIXT_TIME = 1000f
    }
}
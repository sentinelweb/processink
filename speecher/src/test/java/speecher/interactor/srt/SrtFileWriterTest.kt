package speecher.interactor.srt

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.annotations.Fixture
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import speecher.domain.Subtitles
import java.io.File

class SrtFileWriterTest {

    @Fixture
    lateinit var fixtSubs: Subtitles

    private lateinit var testFile: File

    private val sut: SrtFileWriter = SrtFileWriter(SrtMapper())

    @Before
    fun setUp() {
        FixtureAnnotations.initFixtures(this)
        testFile = File("${System.getProperty("user.dir")}/src/test/resouces/speecher/interactor/srt/test_write.srt")
    }

    @After
    fun tearDown() {
        testFile.delete()
    }

    @Test
    fun write() {
        sut.write(fixtSubs, testFile)
        val read = SrtFileReader(SrtMapper()).read(testFile)
        assertEquals(fixtSubs.timedTexts, read.timedTexts)
    }
}
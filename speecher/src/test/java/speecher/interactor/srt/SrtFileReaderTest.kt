package speecher.interactor.srt

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File


class SrtFileReaderTest {

    private lateinit var testFile: File

    private val sut: SrtFileReader = SrtFileReader(SrtMapper())

    @Before
    fun setUp() {
        testFile =
            File("${System.getProperty("user.dir")}/src/test/resouces/speecher/interactor/srt/test_srt_read.en.srt")
    }

    @After
    fun tearDown() {

    }

    @Test
    fun read() {
        val read = sut.read(testFile)
        println("subs.length = ${read.timedTexts.size}")
        assertEquals(2769, read.timedTexts.size)
    }
// todo make resource read work
    //        val resource: URL? = javaClass.classLoader.getResource("test_srt_read.en.srt")
//        testFile = if (resource == null) {
//            throw IllegalArgumentException("file not found!")
//        } else {
//
//            // failed if files have whitespaces or special characters
//            //return new File(resource.getFile());
//            File(resource.toURI())
//        }
}
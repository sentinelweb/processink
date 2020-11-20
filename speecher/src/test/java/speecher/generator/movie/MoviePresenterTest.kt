package speecher.generator.movie

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract.State.*
import speecher.scheduler.SchedulerModule
import java.io.File

class MoviePresenterTest : KoinComponent {

    private lateinit var testApplet: TestPApplet

    private lateinit var sut: MoviePresenter

    @Before
    fun setUp() {
        startKoin {
            modules(
                MoviePresenter.scopeModule,
                SchedulerModule.module,
                TestPApplet.appletModule
            )
        }
        testApplet = get()
        sut = MoviePresenter()
        testApplet.run()

    }

    @After
    fun tearDown() {
        stopKoin()
        sut.cleanup()
    }

    private fun waitFor(sleep: Long = 50, timeout: Long = 5000, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (!condition.invoke()) {
            Thread.sleep(sleep)
            if (System.currentTimeMillis() - start > timeout) {
                assertTrue("Timeout waiting for state", false)
            }
        }
    }

    @Test
    fun testMovieLoad() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)

        waitFor { sut.position >= 2 }

        assertThat(sut.position, `is`(greaterThanOrEqualTo(2f)))
        assertEquals(PLAYING, sut.playState)
    }

    @Test
    fun testMoviePause() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)

        waitFor { sut.position >= 1 }

        sut.pause()
        waitFor { sut.playState == PAUSED }

        assertEquals(PAUSED, sut.playState)
    }

    @Test
    fun testMovieSeek() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        waitFor { sut.playState == PLAYING }

        val time = System.currentTimeMillis()
        sut.seekTo(20f)
        println("sek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { sut.position >= 20.1 }

        assertThat(sut.position, `is`(greaterThanOrEqualTo(20.1f)))
        assertEquals(PLAYING, sut.playState)
    }

    @Test
    fun testMovieNoPlaySeek() {
        val testListener = TestListener()
        sut.listener = testListener
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.volume(0.0f)
        sut.pause()
        waitFor { testListener.readyCalled }

        val time = System.currentTimeMillis()
        sut.seekTo(30f)
        println("seek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { sut.playState == PAUSED }
        sut.play()

        waitFor { sut.position >= 31 }

        assertThat(sut.position, `is`(greaterThanOrEqualTo(31f)))
    }


    @Test
    fun testMoviePauseSeek() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        waitFor { sut.playState == PLAYING }

        sut.pause()
        waitFor { sut.playState == PAUSED }

        val time = System.currentTimeMillis()
        sut.seekTo(20f)
        println("seek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { sut.playState == PAUSED }
        sut.play()

        waitFor { sut.position >= 21 }

        assertThat(sut.position, `is`(greaterThanOrEqualTo(21f)))
    }

    @Test
    fun testMovieSubtitle() {
        val fixSub = Subtitles.Subtitle(10f, 12f, listOf())
        val testListener = TestListener()
        sut.listener = testListener
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        waitFor { testListener.readyCalled }


        val time = System.currentTimeMillis()
        sut.setSubtitle(fixSub)
        println("seek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { testListener.subStartCalled }

        assertEquals(PLAYING, sut.playState)
        assertThat(sut.position, `is`(greaterThanOrEqualTo(10f)))
        assertThat(sut.position, `is`(lessThanOrEqualTo(10.1f)))

        waitFor { testListener.subFinishedCalled }

        assertEquals(PAUSED, sut.playState)
        assertThat(sut.position, `is`(greaterThanOrEqualTo(12f)))
        assertThat(sut.position, `is`(lessThanOrEqualTo(12.1f)))
    }

    private inner class TestListener : MovieContract.Listener {

        var readyCalled = false
        var subStartCalled = false
        var subFinishedCalled = false

        override fun onReady() {
            readyCalled = true
        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {
            subStartCalled = true
        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            subFinishedCalled = true
        }

    }

    @Test
    fun testMovieSpeed() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        waitFor { sut.playState == PLAYING }
        sut.setMovieSpeed(2f)

        val start = System.currentTimeMillis()

        waitFor { sut.position >= 2 }

        val timeTaken = System.currentTimeMillis() - start


        assertThat(sut.position, `is`(greaterThanOrEqualTo(2f)))
        assertThat(sut.position, `is`(lessThanOrEqualTo(2.1f)))
        assertThat(timeTaken, `is`(lessThanOrEqualTo(1050L)))

    }

    companion object {
        val BASE = System.getProperty("user.dir")
        private val BASE_RESOURCES = "$BASE/src/main/resources"

        var DEF_BASE_PATH = "$BASE/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0"
        var DEF_MOVIE_PATH = "$DEF_BASE_PATH.mp4"

        var DEF_SRT_PATH = "$DEF_BASE_PATH.en.srt"
        var DEF_WRITE_SRT_PATH = "$DEF_BASE_PATH.write.srt"
    }
}
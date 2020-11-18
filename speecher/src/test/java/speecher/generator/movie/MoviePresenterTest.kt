package speecher.generator.movie

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertEquals
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
        testApplet.cleanup()
    }

    private fun waitFor(sleep: Long = 50, timeout: Long = 5000, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (!condition.invoke() && System.currentTimeMillis() - start < timeout) {
            Thread.sleep(sleep)
        }
    }

    @Test
    fun testMovieLoad() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)

        waitFor { sut.position >= 2 }

        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(2f)))
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

        waitFor { sut.position >= 20 }

        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(20f)))
        assertEquals(PLAYING, sut.playState)
    }

    @Test
    fun testMovieNoPlaySeek() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.volume(0.0f)
        sut.pause()
        waitFor { sut.playState == PAUSED }

        val time = System.currentTimeMillis()
        sut.seekTo(30f)
        println("seek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { sut.playState == PAUSED }
        sut.play()

        waitFor { sut.position >= 31 }

        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(31f)))
        //assertEquals(PAUSED, sut.playState)
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

        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(21f)))
    }

    @Test
    fun testMovieSubtitle() {
        val fixSub = Subtitles.Subtitle(10f, 12f, listOf())
        val testListener = SubTestListener()
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        waitFor { sut.playState == PLAYING }

        sut.listener = testListener
        val time = System.currentTimeMillis()
        sut.setSubtitle(fixSub)
        println("seek time : ${System.currentTimeMillis() - time}")
        assertEquals(SEEKING, sut.playState)

        waitFor { testListener.subStartCalled }

        assertEquals(PLAYING, sut.playState)
        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(10f)))
        assertThat(sut.position, `is`(Matchers.lessThanOrEqualTo(10.1f)))

        waitFor { testListener.subFinishedCalled }

        assertEquals(PAUSED, sut.playState)
        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(12f)))
        assertThat(sut.position, `is`(Matchers.lessThanOrEqualTo(12.1f)))
    }

    private inner class SubTestListener : MovieContract.Listener {
        var subStartCalled = false
        var subFinishedCalled = false
        override fun onSubtitleStart(sub: Subtitles.Subtitle) {
            subStartCalled = true
        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            subFinishedCalled = true
        }

        override fun onStateChange(state: MovieContract.State) {

        }

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
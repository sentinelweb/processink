package speecher.generator.movie

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import speecher.domain.Subtitles
import speecher.scheduler.SchedulerModule
import java.io.File

class MoviePresenterTest : KoinComponent {

    private lateinit var testApplet: TestPApplet

    private var done = false

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

    }

    @Test
    fun testMovieLoad() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)

        while (sut.position < 2) {
            Thread.sleep(500)
        }

        assertTrue(sut.position > 2)
        assertEquals(MovieContract.State.PLAYING, sut.playState)
    }

    @Test
    fun testMoviePause() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)

        while (sut.position < 1) {
            Thread.sleep(200)
        }

        sut.pause()

        assertEquals(MovieContract.State.PAUSED, sut.playState)
    }

    @Test
    fun testMovieSeek() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        sut.seekTo(20f)

        assertEquals(MovieContract.State.SEEKING, sut.playState)

        while (sut.position < 20) {
            Thread.sleep(500)
        }

        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(20f)))
        assertEquals(MovieContract.State.PLAYING, sut.playState)
    }

    @Test
    fun testMovieSubtitle() {
        val fixSub = Subtitles.Subtitle(10f, 12f, listOf())
        val testListener = SubTestListener()
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        sut.listener = testListener
        sut.setSubtitle(fixSub)

        assertEquals(MovieContract.State.SEEKING, sut.playState)

        while (!testListener.subStartCalled) {
            Thread.sleep(50)
        }

        assertEquals(MovieContract.State.PLAYING, sut.playState)
        assertThat(sut.position, `is`(Matchers.greaterThanOrEqualTo(10f)))
        assertThat(sut.position, `is`(Matchers.lessThanOrEqualTo(10.1f)))

        while (!testListener.subFinishedCalled) {
            Thread.sleep(50)
        }

        assertEquals(MovieContract.State.PAUSED, sut.playState)
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
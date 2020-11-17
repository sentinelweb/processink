package speecher.generator.movie

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
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

    }

    @Test
    fun testMovieLoad() {
        sut.openMovie(File(DEF_MOVIE_PATH))
        sut.play()
        sut.volume(0.2f)
        Thread.sleep(2000)

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
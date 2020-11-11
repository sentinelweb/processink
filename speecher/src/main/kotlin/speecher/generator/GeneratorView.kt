package speecher.generator

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import speecher.scheduler.ProcessingExecutor
import java.awt.Dimension
import java.awt.geom.Rectangle2D
import java.io.File

class GeneratorView constructor(
    private val presenter: GeneratorContract.Presenter,
    private val state: GeneratorState,
    private val pExecutor: ProcessingExecutor
) : PApplet(), GeneratorContract.View {

    private lateinit var f: PFont
    private var activeMovie: Int? = null
    private val movies: MutableMap<Int, MovieData> = mutableMapOf()

    // todo move to state
    data class MovieData constructor(
        var movie: Movie? = null,
        var movieDimension: Dimension? = null,
        var screenRect: Rectangle2D.Float? = null,
        var duration: Float? = null,
        var position: Float? = null,
        var playState: State = State.INIT
    ) {
        enum class State { INIT, LOADED, PLAYING, PAUSED, STOPPED, SEEKING }

        val isInitialised: Boolean
            get() = movieDimension != null
    }

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${LIB_PATH}/plugins/")
    }


    // region Processing

    override fun settings() {
        size(1024, 768, PConstants.P2D)
    }

    override fun setup() {
        background(0)
        // Create the font
        printArray(PFont.list())
        f = createFont("Thonburi", 24f)
        textFont(f)
        textSize(20f)
        textAlign(PConstants.CENTER, PConstants.CENTER)
        presenter.initialise()
    }

    override fun draw() {
        while (pExecutor.workQueue.size > 0) {
            pExecutor.workQueue.take().run()
        }
        background(0)
        fill(255f, 255f, 255f)
        activeMovie?.let {
            movies[it]?.apply {
                if (isInitialised) {
                    movies[it]?.screenRect?.apply {
                        image(movie, x, y, width, height)
                    }
                }
            }

        }
        fill(255f, 255f, 0f)
        presenter.subtitle?.let { text(it, width / 2f, height - 25f) }
    }
    // endregion

    // region movie
    fun movieEvent(m: Movie) {
        m.read()
        // println("FR: ${myMovie.frameRate} W:${myMovie.width} H:${myMovie.height}")
        movies.values.find { it.movie == m }
            ?.let { movieData ->
                movieData.movie?.let { movie ->
                    if (!movieData.isInitialised) {
                        movieData.movieDimension = Dimension(movie.width, movie.height)
                        val movieAspect = movieData.movieDimension!!.width / movieData.movieDimension!!.height.toFloat()
                        val screenAspect = width / height.toFloat()
                        movieData.screenRect = Rectangle2D.Float(
                            0f,
                            ((height - height * screenAspect / movieAspect) / 2f),
                            width.toFloat(),
                            (width / movieAspect)
                        )
                        movieData.duration = movie.duration()
                        movieData.playState = MovieData.State.LOADED
                    }
                    movieData.position = movie.time().apply {
                        presenter.onMovieEvent(movies.filter { it.value.movie == movie }.keys.toList()[0], this)
                    }
                    movieData.playState = MovieData.State.PLAYING

                }
            }
    }
    // endregion

    // region View

    override fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun openMovie(i: Int, file: File) {
        if (movies.size <= i) {
            movies[i] = MovieData()
        }
        movies[i]?.apply {
            if (isInitialised) {
                movie?.stop()
                movie?.dispose()
            }
            movieDimension = null
            screenRect = null
            movie = Movie(this@GeneratorView, file.absolutePath)
            movie?.play()
        }


    }

    override fun setMovieSpeed(i: Int, speed: Float) {
        movies[i]?.apply { if (isInitialised) pExecutor.execute { movie?.speed(speed) } }
    }

    override fun play(i: Int) {
        movies[i]?.apply {
            if (isInitialised) {
                pExecutor.execute { movie?.play() }
                playState = MovieData.State.PLAYING
            }
        }
    }

    override fun pause(i: Int) {
        movies[i]?.apply {
            if (isInitialised) {
                pExecutor.execute { movie?.pause() }
                playState = MovieData.State.PAUSED
            }
        }
    }

    override fun volume(i: Int, vol: Float) {
        movies[i]?.apply { if (isInitialised) pExecutor.execute { movie?.volume(vol) } }
    }

    override fun seekTo(i: Int, positionSec: Float) {
        movies[i]?.apply {
            if (isInitialised) pExecutor.execute { movie?.jump(positionSec) }
        }
    }

    override fun setActive(i: Int?) {
        activeMovie = i
    }

    companion object {
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"
    }
}
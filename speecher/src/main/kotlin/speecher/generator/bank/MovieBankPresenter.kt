package speecher.generator.bank

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.context.KoinContextHandler.get
import org.koin.core.qualifier.named
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.bank.MovieBankContract.PlayState.*
import speecher.generator.movie.MovieContract
import speecher.generator.movie.MovieCreator
import speecher.scheduler.SchedulerModule
import speecher.util.wrapper.LogWrapper
import java.io.File
import java.util.concurrent.TimeUnit


class MovieBankPresenter(
    private val state: MovieBankContract.State,
    private val view: MovieBankContract.View,
    private val bankSize: Int
) : MovieBankContract.Presenter, MovieBankContract.External {

    private val koin = get()
    private val pScheduler: Scheduler = koin.get(named(SchedulerModule.PROCESSING))
    private val swingScheduler: Scheduler = koin.get(named(SchedulerModule.SWING))
    private val log: LogWrapper = koin.get()
    private val movieCreator: MovieCreator = koin.get()

    init {
        log.tag(this)
    }

    private val movies = mutableListOf<MovieContract.External>()

    override var listener: MovieBankContract.Listener? = null

    override var config: MovieBankContract.Config = MovieBankContract.Config()
        get() = field
        set(value) {
            field = value
            state.words = config.words
            movies.forEach {
                it.config = it.config.copy(playEventLatency = value.playEventLatency)

            }
        }

    override var playState: MovieBankContract.PlayState = NOT_INIT
        set(v) {
            field = v
            listener?.onStateChanged()
        }

    override val subtitleToDisplay: String
        get() = state.movieToWordMap[state.activeIndex]?.sub?.text?.get(0) ?: "-"

    val disposables: CompositeDisposable = CompositeDisposable()

    private fun wordAtIndex(index: Int): Sentence.Word? = state.words?.words?.let {
        if (index < it.size) it.get(index) else null
    }

    private fun Int.wrapInc() = if (this + 1 < movies.size) this + 1 else 0

    override fun pause() {
        movies.forEach { it.pause() }
        playState = PAUSED
    }

    override fun loadMovieFile(movie: File) {
        playState = LOADING
        openMovieSingle(movie)
            .doOnSuccess {
                state.loadingWord = -1
                movies.forEachIndexed { i, movie ->
                    movie.pause()
                }
            }
            .subscribe({
                println("Opened movie file : $it")
                playState = LOADED
                if (state.playAfterLoaded) {
                    startPlaying()
                }
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    // region playback
    override fun startPlaying() {
        if (playState == LOADING) {
            state.playAfterLoaded = true
            return
        }
        playState = PLAYING
        log.startTime()
        movies.forEach {
            it.volume(config.volume)
            it.setMovieSpeed(config.playSpeed, false)
        }
        state.loadingWord = 0
        state.activeIndex = 0
        wordAtIndex(state.loadingWord)?.let {
            state.movieToWordMap[state.activeIndex] = it
            movies[state.activeIndex].setMovieSpeed(config.playSpeed, false)
            movies[state.activeIndex].setSubtitle(it.sub)
        }

        movies[state.activeIndex].play()
        (1..movies.size - 1).forEach { i ->
            incrementWordIndex()
            wordAtIndex(state.loadingWord)?.let {
                log.d("next for $i ${it.sub}")
                state.movieToWordMap[i] = it
                movies[i].setSubtitle(it.sub)
            }
        }
    }

    private fun subtitleFinished() {
        //view.active = -1
        log.d("finished(${state.activeIndex})")
        movies[state.activeIndex].volume(0f)
        movies[state.activeIndex].pause()
        val lastIndex = state.activeIndex
        state.activeIndex = state.activeIndex.wrapInc()
        state.movieToWordMap[state.activeIndex]?.let {
            //view.active = state.activeIndex
            if (!config.playOneWordAtATime) {
                continuePlaying()
            } else {
                playState = PAUSED
            }
        } ?: run {
            log.d("Nothing to play")
            listener?.onPlayFinished()
            view.active = -1
            playState = COMPLETE
        }
        loadNextWord(lastIndex)
    }

    override fun continuePlaying() {
        movies[state.activeIndex].let {
            it.volume(config.volume * (state.movieToWordMap[state.activeIndex]?.vol ?: 1f))
            it.play()
            log.d("playing(${state.activeIndex}) - ${it.getText()}")
        }
    }

    private fun loadNextWord(playerIndex: Int) {
        incrementWordIndex()
        wordAtIndex(state.loadingWord)?.let {
            log.d("next for $playerIndex ${it.sub}")
            movies[playerIndex].setMovieSpeed(config.playSpeed * it.speed, false)
            movies[playerIndex].setSubtitle(it.sub)
            state.movieToWordMap[playerIndex] = it
            log.d(it.sub.text[0])
        } ?: run {
            state.movieToWordMap[playerIndex] = null
            log.d("No more words to load playerIndex=${playerIndex} wordIndex=${state.loadingWord}")
        }
    }

    private fun incrementWordIndex() {
        state.loadingWord++
        if (config.looping) {
            state.loadingWord = state.loadingWord % (state.words?.words?.size ?: 0)
        }
    }
    // endregion

    // region Movie
    private fun openMovieSingle(file: File): Single<File> =
        Single.just(file)
            .observeOn(pScheduler)
            .doOnSuccess {
                movies.forEachIndexed { i, movie ->
                    movie.cleanup()
                }
                movies.clear()
            }
            .doOnSuccess {
                (0..bankSize - 1).forEach {
                    makeMovie(it, file)
                }
            }

    private fun makeMovie(i: Int, file: File) = movieCreator.create(i, log, MvListener(i)).also {
        movies.add(it)
        it.openMovie(file)
        it.volume(0f)
        view.addMovieView(it.view)
    }

    override fun cleanup() {
        movies.forEach { it.cleanup() }
        view.cleanup()
        playState = INIT
    }

    inner class MvListener(private val index: Int) : MovieContract.Listener {

        override fun onReady() {

        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {
            //log.d("onSubtitleStart ${state.activeIndex}")
        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            log.d("onSubtitleFinished ${state.activeIndex}")
            val wordSpacing = config.wordSpaceTime.toLong() +
                    (state.movieToWordMap[state.activeIndex]?.let { (it.spaceAfter * 1000f).toLong() } ?: 0L)
            if (wordSpacing > 0) {
                movies[state.activeIndex].volume(0f)
                movies[state.activeIndex].pause()
                Completable
                    .timer(wordSpacing, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .subscribe(
                        { subtitleFinished() },
                        { log.e("error wordspacing", it) }
                    )
            } else {
                subtitleFinished()
            }
        }

        override fun onPlaying() {
            log.d("onPlaying($index active=${state.activeIndex})")
            if (index == state.activeIndex) {
                view.active = state.activeIndex
            }
        }

    }
    // endregion
}
package speecher.generator.bank

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.context.KoinContextHandler.get
import org.koin.core.qualifier.named
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract
import speecher.generator.movie.MoviePresenter
import speecher.scheduler.SchedulerModule
import speecher.util.wrapper.LogWrapper
import java.io.File


class MovieBankPresenter(
    private val state: MovieBankState,
    private val view: MovieBankContract.View,
    private val bankSize: Int
) : MovieBankContract.Presenter, MovieBankContract.External, MovieContract.Parent {

    private val koin = get()
    private val pScheduler: Scheduler = koin.get(named(SchedulerModule.PROCESSING))
    private val swingScheduler: Scheduler = koin.get(named(SchedulerModule.SWING))
    private val log: LogWrapper = koin.get()

    override val playEventLatency: Float? = 0.05f // todo link to

    private val movies = mutableListOf<MovieContract.External>()

    override var listener: MovieBankContract.Listener? = null
    override var words: Sentence?
        get() = state.words
        set(value) {
            state.words = value
        }
    override var looping: Boolean
        get() = state.looping
        set(value) {
            state.looping = value
        }
    override var volume: Float
        get() = state.volume
        set(value) {
            state.volume = value
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
    }

    override fun loadMovieFile(movie: File) {
        openMovieSingle(movie)
            .doOnSuccess {
                state.loadingWord = -1
                movies.forEachIndexed { i, movie ->
                    movie.pause()
                }
            }
            .subscribe({
                println("Opened movie file : $it")
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    // region playback
    fun startPlaying() {
        log.startTime()
        movies.forEach {
            it.volume(state.volume)
        }
        state.loadingWord = 0
        state.activeIndex = 0
        wordAtIndex(state.loadingWord)?.let {
            state.movieToWordMap[state.activeIndex] = it
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

    private fun playNext() {
        //view.active = -1
        log.d("finished(${state.activeIndex})")
        movies[state.activeIndex].volume(0f)
        movies[state.activeIndex].pause()
        val lastIndex = state.activeIndex
        state.activeIndex = state.activeIndex.wrapInc()

        state.movieToWordMap[state.activeIndex]?.let {
            //view.active = state.activeIndex
            movies[state.activeIndex].volume(state.volume)
            movies[state.activeIndex].play()
            log.d("playing(${state.activeIndex}) - ${it.sub.text}")
        } ?: run {
            log.d("Nothing to play")
            listener?.onPlayFinished()
            view.active = -1
        }
        loadNextWord(lastIndex)
    }

    private fun loadNextWord(playerIndex: Int) {
        incrementWordIndex()
        wordAtIndex(state.loadingWord)?.let {
            log.d("next for $playerIndex ${it.sub}")
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
        if (state.looping) {
            state.loadingWord = state.loadingWord % (state.words?.words?.size ?: 0)
        }
    }
    // endregion

    // region Movie
    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
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
    }

    private fun makeMovie(i: Int, file: File) = MoviePresenter(i, log, view).apply {
        listener = MvListener(i)
        parent = this@MovieBankPresenter
        movies.add(this)
        openMovie(file)
        volume(0f)
//        pause()
    }

    inner class MvListener(private val index: Int) : MovieContract.Listener {

        override fun onReady() {

        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {
            //log.d("onSubtitleStart ${state.activeIndex}")
        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            log.d("onSubtitleFinished ${state.activeIndex}")
            playNext()
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
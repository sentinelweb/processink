package speecher.editor

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import speecher.domain.Subtitles
import speecher.editor.sublist.SubListContract
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.interactor.srt.SrtInteractor
import java.io.File

class EditorPresenter constructor(
    private val view: EditorContract.View,
    private val state: EditorState,
    private val transport: TransportContract.External,
    private val srtInteractor: SrtInteractor,
    private val readSubs: SubListContract.External,
    private val writeSubs: SubListContract.External,
    private val pScheduler: Scheduler,
    private val swingScheduler: Scheduler

) : EditorContract.Presenter, TransportContract.StateListener {

    private val disposables: CompositeDisposable = CompositeDisposable()

    // region SubListContract.Listener - Read & Write
    private val readSubListListener = object : SubListContract.Listener {
        override fun onItemSelected(sub: Subtitles.Subtitle, index: Int) {
            val positionSec = sub.fromSec
            jumpTo(positionSec)
            setLooping(sub.fromSec, sub.toSec)
        }
    }

    private val writeSubListListener = object : SubListContract.Listener {
        override fun onItemSelected(sub: Subtitles.Subtitle, index: Int) {

        }
    }
    // endregion

    init {
        transport.listener = this
        transport.showWindow()
        readSubs.listener = readSubListListener
        writeSubs.listener = writeSubListListener
        disposables.add(
            transport.events()
                .subscribe({
                    println("editor event: ${it.uiEventType} -> ${it.data}")
                    processEvent(it)
                }, {
                    println("error: ${it.localizedMessage}")
                    it.printStackTrace()
                })
        )
    }

    // region Presenter
    override val currentReadSubtitle: String?
        get() = if (state.currentReadIndex > -1) {
            state.srtRead?.timedTexts?.get(state.currentReadIndex)?.text?.toString()//joinToString { "," }
        } else null

    override val currentWriteSubtitle: String?
        get() = if (state.currentWriteIndex > -1) {
            state.srtWrite?.timedTexts?.get(state.currentWriteIndex)?.text?.joinToString { "," }
        } else null

    override fun duration(dur: Float) {
        state.movieDurationSec = dur
        transport.setDuration(dur)
    }

    override fun position(pos: Float) {
        state.moviePositionSec = pos
        transport.setPosition(pos)
        checkReadSubtitle(pos)
        checkLoopingPos(pos)
    }

    override fun setPlayState(mode: TransportContract.UiDataType) {
        transport.setPlayState(mode)
    }

    override fun movieInitialised() {
        transport.updateState()
    }

    override fun initialise() {
        setMovieFile(File(EditorView.DEF_MOVIE_PATH))
        readSubs.showWindow()
        readSubs.setTitle("Read Subtitles")
        writeSubs.showWindow()
        writeSubs.setTitle("Write Subtitles")
        setReadSrt(File(EditorView.DEF_SRT_PATH))
    }
    // endregion

    // region TransportContract.StateListener
    override fun speed(speed: Float) {
        view.setMovieSpeed(speed)
    }
    // endregion

    private fun setMovieFile(file: File) {
        state.movieFile = file
        view.openMovie(file)
        transport.setMovieTitle(file.name)
        transport.speed = 1f
    }

    private fun setReadSrt(file: File) {
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())
            .blockingSubscribe({
                state.srtRead = it
                state.srtReadFile = file
                transport.setSrtReadTitle(file.name)
                readSubs.setList(it)
            }, {
                it.printStackTrace()
            })
    }

    private fun processEvent(uiEvent: TransportContract.UiEvent) {
        when (uiEvent.uiEventType) {
            PLAY -> {
                view.play()
            }
            PAUSE -> {
                view.pause()
            }
            MUTE -> Unit
            SEEK -> {
                state.movieDurationSec?.apply { jumpTo((uiEvent.data as Float) * this) }
            }
            VOLUME_CHANGED -> {
                view.volume(uiEvent.data as Float)
            }
            MENU_FILE_OPEN_MOVIE -> {
                transport.showOpenDialog("Open Movie", state.movieFile?.parentFile) { file ->
                    setMovieFile(file)
                }
            }
            MENU_FILE_OPEN_SRT_READ -> {
                transport.showOpenDialog("Open SRT for read", state.movieFile?.parentFile) { file ->
                    setReadSrt(file)
                }
            }
            MENU_FILE_NEW_SRT_WRITE -> {
                state.srtWriteFile = null
                state.srtWrite = Subtitles(listOf())
                transport.setSrtWriteTitle("New")
            }
            MENU_FILE_OPEN_SRT_WRITE -> {
                state.srtWriteFile = null
                transport.showOpenDialog("Open SRT for write", state.movieFile?.parentFile) { file ->
                    srtInteractor.read(file).subscribe {
                        state.srtWrite = it
                        state.srtWriteFile = file
                        transport.setSrtWriteTitle(file.name)
                    }
                }
            }
            MENU_FILE_SAVE_SRT -> {
                transport.showSaveDialog("Save SRT", state.movieFile?.parentFile) { file ->

                }
            }
            MENU_FILE_EXIT -> {
                System.exit(0)
            }
            MENU_VIEW_READ_SUBLIST -> {
                readSubs.showWindow()
            }
            MENU_VIEW_WRITE_SUBLIST -> {
                writeSubs.showWindow()
            }
            LOOP -> {
                if (!(uiEvent.data as Boolean)) {
                    state.loopEndSec = null
                    state.loopStartSec = null
                }
            }
            else -> Unit
        }
        println("EditPresenter: transport.event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
    }

    private fun jumpTo(positionSec: Float) {
        println("jumpTo: $positionSec")
        // fixme subtitles are syncing up properly after jump
        state.currentReadIndex = -1
        state.lastReadIndex = -1
        view.seekTo(positionSec)
        scanForReadSubtitle(positionSec)
    }

    private fun setLooping(fromSec: Float, toSec: Float) {
        println("setLooping: $fromSec -> $toSec")
        state.loopStartSec = fromSec
        state.loopEndSec = toSec
        transport.setLooping(true)
    }

    private fun checkLoopingPos(pos: Float) {
        state.loopEndSec?.let { loopEnd ->
            state.loopStartSec?.let { loopStart ->
                if (pos > loopEnd) {
                    jumpTo(loopStart)
                }
            }
        }
    }

    private fun checkReadSubtitle(pos: Float) {
        @Suppress("ControlFlowWithEmptyBody")
        if (state.currentReadIndex > -1
            && (state.srtRead?.timedTexts?.get(state.currentReadIndex)?.between(pos) ?: false)
        ) {

        } else if (state.currentReadIndex > -1) {
            state.lastReadIndex = state.currentReadIndex
            state.currentReadIndex = -1
        } else {
            scanForReadSubtitle(pos)
        }
    }

    private fun scanForReadSubtitle(positionSec: Float) {
        state.srtRead?.timedTexts?.let { texts ->
            val startIndex = state.lastReadIndex
            (startIndex + 1..texts.size - 1).forEach { testIndex ->
                val get = texts.get(testIndex)
                if (get.between(positionSec)) {
                    state.currentReadIndex = testIndex
                    return
                }
                if (get.toSec < positionSec) {
                    state.lastReadIndex = testIndex
                    return
                }
            }
        }
        state.lastReadIndex = -1
    }


}
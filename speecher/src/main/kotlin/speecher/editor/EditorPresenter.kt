package speecher.editor

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Subtitles
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.interactor.srt.SrtInteractor
import java.io.File

class EditorPresenter constructor(
    private val view: EditorContract.View,
    private val state: EditorState,
    private val transport: TransportContract.External,
    private val srtInteractor: SrtInteractor
) : EditorContract.Presenter, TransportContract.StateListener {

    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        transport.setStateListener(this)
        disposables.add(
            transport.events()
                .subscribe({
                    println("editor event: ${it.uiEventType} -> ${it.data}")
                    processEvent(it)
                }, {
                    println("error: ${it.localizedMessage}")
                    //it.printStackTrace()
                })
        )
    }

    // region StateListener
    override fun speed(speed: Float) {
        view.setMovieSpeed(speed)
    }
    // endregion

    // region Presenter
    override fun duration(dur: Float) {
        state.movieDurationSec = dur
        transport.setDuration(dur)
    }

    override fun position(pos: Float) {
        state.moviePositionSec = pos
        transport.setPosition(pos)
    }

    override fun setPlayState(mode: TransportContract.UiDataType) {
        transport.setPlayState(mode)
    }

    override fun movieInitialised() {
        transport.updateState()
    }

    override fun initialise() {
        setMovieFile(File(EditorView.MOVIE_PATH))
    }
    // endregion

    private fun setMovieFile(file: File) {
        state.movieFile = file
        view.openMovie(file)
        transport.setTitle(file.name)
        transport.speed = 1f
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
                state.movieDurationSec?.apply { view.seekTo((uiEvent.data as Float) * this) }
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
                    srtInteractor.read(file).subscribe {
                        state.srtRead = it
                        state.srtReadFile = file
                        transport.setSrtReadTitle(file.name)
                    }
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
                // todo prompt confirm and save state
                System.exit(0)
            }
            else -> println("event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
        }
    }

}
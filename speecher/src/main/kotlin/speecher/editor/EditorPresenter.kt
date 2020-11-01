package speecher.editor

import io.reactivex.disposables.CompositeDisposable
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.interactor.srt.SrtInteractor

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

    private fun processEvent(uiEvent: TransportContract.UiEvent) {
        when (uiEvent.uiEventType) {
            PLAY -> {
                view.play()
            }
            PAUSE -> {
                view.pause()
            }
            MUTE -> Unit
            FWD -> Unit
            REW -> Unit
            SEEK -> {
                state.movieDurationSec?.apply { view.seekTo((uiEvent.data as Float) * this) }
            }
            VOLUME_CHANGED -> {
                view.volume(uiEvent.data as Float)
            }
            MENU_FILE_OPEN_MOVIE -> {
                transport.showOpenDialog("Open Movie") { file ->
                    state.movieFile = file
                    view.openMovie(file)
                    transport.setTitle(file.name)
                    transport.speed = 1f
                }
            }
            MENU_FILE_OPEN_SRT_READ -> {
                transport.showOpenDialog("Open SRT for read") { file ->
                    state.srtReadFile = file
                }
            }
            MENU_FILE_OPEN_SRT_WRITE -> {
                transport.showOpenDialog("Open SRT for write") { file ->
                    state.srtWriteFile = file
                }
            }
            MENU_FILE_SAVE_SRT -> {
                transport.showSaveDialog("Save SRT") { file ->

                }
            }
            MENU_FILE_EXIT -> {
                // todo prompt confirm and save state
                System.exit(0)
            }
            else -> println("event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
        }
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
    // endregion

}
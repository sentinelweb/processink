package speecher.editor.transport

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.editor.transport.TransportContract.UiData
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.util.format.TimeFormatter
import java.io.File
import javax.swing.JFileChooser

class TransportPresenter constructor(
    private val view: TransportContract.View,
    private val state: TransportState,
    private val timeFormatter: TimeFormatter
) : TransportContract.Presenter, TransportContract.External {

    private val disposables: CompositeDisposable = CompositeDisposable()

    private val updates: Subject<UiData> = BehaviorSubject.create()
    override val updateObservable: Subject<UiData> = updates
    private lateinit var listener: TransportContract.StateListener

    override var speed: Float = 1f
        get() = state.speed
        set(value) {
            field = value
            state.speed = value
            listener.speed(value)
            updates.onNext(UiData(SPEED, state.speed))
        }

    init {
        disposables.add(
            view.events.subscribe({
                processEvent(it)
            }, {
                println("error: ${it.localizedMessage}")
                it.printStackTrace()
            })
        )
        view.presenter = this
        view.showWindow()
    }

    private fun processEvent(uiEvent: TransportContract.UiEvent) = when (uiEvent.uiEventType) {
        MUTE -> updates.onNext(UiData(MUTED, !(uiEvent.data as Boolean)))
        FWD -> speed *= 1.1f
        REW -> speed /= 1.1f
        VOLUME_CHANGED -> state.volume = uiEvent.data as Float
        else -> println("event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
    }

    // region Presenter

    // endregion

    // region External
    override fun events(): Observable<TransportContract.UiEvent> = view.events

    override fun setTitle(title: String) {
        updates.onNext(UiData(TITLE, title))
    }

    override fun setDuration(dur: Float) {
        state.durSec = dur
        updates.onNext(UiData(DURATION, timeFormatter.formatTime(dur)))
    }

    override fun setPosition(pos: Float) {
        state.posSec = pos
        //if (System.currentTimeMillis() - state.positionLastUpdate > 250) {
        updates.onNext(UiData(POSITION, timeFormatter.formatTime(pos)))
        updates.onNext(UiData(POSITION_SLIDER, state.posSec / state.durSec))
        //    state.positionLastUpdate = System.currentTimeMillis()
        //}
    }

    override fun setPlayState(mode: TransportContract.UiDataType) = when (mode) {
        MODE_PLAYING -> updates.onNext(UiData(MODE_PLAYING))
        MODE_PAUSED -> updates.onNext(UiData(MODE_PAUSED))
        else -> throw IllegalArgumentException("only MODE_PLAYING or MODE_PAUSED for setPlayState")
    }

    override fun setVolume(volume: Float) {
        state.volume = volume
    }

    override fun updateState() {
        listener.speed(state.speed)
        updates.onNext(UiData(VOLUME, state.volume))
    }

    override fun setSrtReadTitle(name: String) {
        updates.onNext(UiData(READ_SRT, name))
    }

    override fun setSrtWriteTitle(name: String) {
        updates.onNext(UiData(WRITE_SRT, name))
    }

    override fun setStateListener(listener: TransportContract.StateListener) {
        this.listener = listener
    }

    override fun showOpenDialog(title: String, currentDir: File?, chosen: (File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir.let { currentDirectory = it }
            val result = showOpenDialog(view.component)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }

    override fun showSaveDialog(title: String, currentDir: File?, chosen: (File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir.let { currentDirectory = it }
            val result = showSaveDialog(view.component)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }
    // endregion

}
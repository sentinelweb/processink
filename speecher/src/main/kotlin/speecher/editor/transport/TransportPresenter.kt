package speecher.editor.transport

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.editor.transport.TransportContract.UiData
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.util.format.TimeFormatter
import java.io.File
import javax.swing.JFileChooser

class TransportPresenter : TransportContract.Presenter, TransportContract.External {

    private val scope = this.getOrCreateScope()
    private val view: TransportContract.View = scope.get()
    private val state: TransportState = scope.get()
    private val timeFormatter: TimeFormatter = scope.get()

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
    }

    private fun processEvent(uiEvent: TransportContract.UiEvent) {
        println("event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
        when (uiEvent.uiEventType) {
            MUTE -> state.muted = uiEvent.data as Boolean
            FWD -> speed *= 1.1f
            REW -> speed /= 1.1f
            VOLUME_CHANGED -> state.volume = uiEvent.data as Float
            else -> Unit
        }
    }

    // region Presenter

    // endregion

    // region External
    override fun events(): Observable<TransportContract.UiEvent> = view.events

    override fun setMovieTitle(title: String) {
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

    override fun showWindow() {
        view.showWindow()
    }

    override fun setLooping(looping: Boolean) {
        updates.onNext(UiData(TransportContract.UiDataType.LOOP, state.loop))
    }

    override fun setStateListener(listener: TransportContract.StateListener) {
        this.listener = listener
    }

    // todo move to view
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

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<TransportPresenter>()) {
                scoped<TransportContract.View> { TransportView(get()) }
                scoped { TransportState() }
            }
        }
        // todo move to transport

    }
}
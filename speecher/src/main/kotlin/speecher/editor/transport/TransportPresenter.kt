package speecher.editor.transport

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.editor.transport.TransportContract.UiData
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEventType.*

class TransportPresenter constructor(
    private val view: TransportContract.View,
    private val state: TransportState
) : TransportContract.Presenter, TransportContract.External {

    private val disposables: CompositeDisposable = CompositeDisposable()

    private val updates: Subject<UiData> = BehaviorSubject.create()
    override val updateObservable: Subject<UiData> = updates
    override fun events(): Observable<TransportContract.UiEvent> = view.events

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

    private fun processEvent(uiEvent: TransportContract.UiEvent) = when (uiEvent.uiEventType) {
        PLAY -> updates.onNext(UiData(MODE_PLAYING))
        PAUSE -> updates.onNext(UiData(MODE_PAUSED))
        MUTE -> updates.onNext(UiData(MUTED, !(uiEvent.data as Boolean)))
        FWD -> {
            state.speed += 1
            updates.onNext(UiData(SPEED, state.speed))
        }
        REW -> {
            state.speed -= 1
            updates.onNext(UiData(SPEED, state.speed))
        }
        else -> println("event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
    }
}
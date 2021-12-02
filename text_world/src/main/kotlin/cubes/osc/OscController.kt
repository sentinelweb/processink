package cubes.osc

import cubes.CubesContract.BackgroundShaderType
import cubes.CubesContract.Control.*
import cubes.CubesContract.Event
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.util.wrapper.LogWrapper

class OscController(
    private val receiver: OscContract.Receiver,
    private val eventMapper: OscEventMapper,
    private val log: LogWrapper
) : OscContract.Controller, OscContract.External {

    init {
        log.tag(this)
        receiver.setController(this)
    }

    private val events: Subject<Event> = BehaviorSubject.create()
    override fun events(): Observable<Event> = events
    override lateinit var listener: OscContract.Listener

    override fun initialise() {
        log.d("Osc receiver initialising ..")
        receiver.start(PORT)
        log.d("Osc receiver started ..")
    }

    override fun processEvent(e: OscContract.OscEvent) {
        try {
            when (e.message) {
                "/background/color" -> events.onNext(Event(SHADER_BG_COLOR, eventMapper.getColor(e)))
                "/background" -> events.onNext(
                    Event(
                        SHADER_BG,
                        BackgroundShaderType.valueOf(eventMapper.getString(e, 0))
                    )
                )
                // cubes
                "/cubes/visible" -> events.onNext(Event(CUBES_VISIBLE, eventMapper.getBoolean(e, 0)))
                "/cubes/rotation/reset" -> events.onNext(Event(CUBES_ROTATION_RESET, null))
                "/cubes/rotation/align" -> events.onNext(Event(CUBES_ROTATION_ALIGN, null))
                "/cubes/rotation/speed/base" -> events.onNext(Event(CUBES_ROTATION_SPEED, eventMapper.getFloat(e, 0)))
                "/cubes/rotation/speed/offset" -> events.onNext(
                    Event(
                        CUBES_ROTATION_OFFEST_SPEED,
                        eventMapper.getFloat(e, 0)
                    )
                )
                "/cubes/fill/alpha" -> events.onNext(Event(CUBES_COLOR_FILL_ALPHA, eventMapper.getInt0To255(e, 0)))
                "/cubes/fill/color/start" -> events.onNext(Event(CUBES_COLOR_FILL_START, eventMapper.getColor(e)))
                "/cubes/fill/color/end" -> events.onNext(Event(CUBES_COLOR_FILL_END, eventMapper.getColor(e)))
                "/cubes/stroke/color" -> events.onNext(Event(CUBES_COLOR_STROKE, eventMapper.getColor(e)))
                "/cubes/stroke/width" -> events.onNext(Event(CUBES_STROKE_WEIGHT, (eventMapper.getFloat(e, 0))))
                "/cubes/stroke/visible" -> events.onNext(Event(CUBES_STROKE, eventMapper.getBoolean(e, 0)))
                else -> {
                    log.d("no handler for message ${e.message}")
                }
            }
        } catch (ex: Throwable) {
            log.e("Cannot process message: ${e.message}", ex)
        }
    }

    override fun shutdown() {
        if (this::listener.isInitialized) {
            listener.onReceiverStopped()
        }
        receiver.shutdown()
    }

    override fun isRunning(): Boolean = receiver.isRunning()


    companion object {
        private const val PORT = 1239
    }
}



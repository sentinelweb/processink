package cubes.osc

import cubes.CubesContract
import cubes.CubesContract.BackgroundShaderType
import cubes.CubesContract.Control.*
import cubes.CubesContract.Event
import cubes.CubesContract.Model3D.TERMINATOR
import cubes.CubesContract.TextTransition.FADE
import cubes.models.TextList.Ordering.INORDER
import cubes.util.wrapper.FilesWrapper
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.util.wrapper.LogWrapper
import java.io.File

class OscController(
    private val receiver: OscContract.Receiver,
    private val eventMapper: OscEventMapper,
    private val log: LogWrapper,
    private val files: FilesWrapper
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
                // toplevel
                "/state/load" -> events.onNext(
                    Event(MENU_OPEN_STATE, File(files.stateDir, eventMapper.getString(e, 0)))
                )
                "/background/color" -> events.onNext(Event(BG_COLOR, eventMapper.getColor(e)))
                "/background" -> events.onNext(
                    // todo get enum
                    Event(SHADER_BG, BackgroundShaderType.valueOf(eventMapper.getString(e, 0)))
                )
                "/animation/time" -> events.onNext(Event(MOTION_ANIMATION_TIME, eventMapper.getFloat(e, 0)))

                // cubes
                "/cubes/visible" -> events.onNext(Event(CUBES_VISIBLE, eventMapper.getBoolean(e, 0)))
                "/cubes/rotation/reset" -> events.onNext(Event(CUBES_ROTATION_RESET, null))
                "/cubes/rotation/align" -> events.onNext(Event(CUBES_ROTATION_ALIGN, null))
                "/cubes/rotation/speed/base" -> events.onNext(Event(CUBES_ROTATION_SPEED, eventMapper.getFloat(e, 0)))
                "/cubes/rotation/speed/offset" -> events.onNext(
                    Event(CUBES_ROTATION_OFFEST_SPEED, eventMapper.getFloat(e, 0))
                )
                "/cubes/rotation/axis/x" -> events.onNext(
                    Event(CUBES_ROTATION, Pair(CubesContract.RotationAxis.X, eventMapper.getBoolean(e, 0)))
                )
                "/cubes/rotation/axis/y" -> events.onNext(
                    Event(CUBES_ROTATION, Pair(CubesContract.RotationAxis.Y, eventMapper.getBoolean(e, 0)))
                )
                "/cubes/rotation/axis/z" -> events.onNext(
                    Event(CUBES_ROTATION, Pair(CubesContract.RotationAxis.Z, eventMapper.getBoolean(e, 0)))
                )
                "/cubes/formation" -> events.onNext(
                    Event(CUBES_FORMATION, CubesContract.Formation.valueOf(eventMapper.getString(e, 0)))
                )
                "/cubes/fill" -> events.onNext(Event(CUBES_FILL, eventMapper.getBoolean(e, 0)))
                "/cubes/fill/alpha" -> events.onNext(Event(CUBES_COLOR_FILL_ALPHA, eventMapper.getInt0To255(e, 0)))
                "/cubes/fill/color/start" -> events.onNext(Event(CUBES_COLOR_FILL_START, eventMapper.getColor(e)))
                "/cubes/fill/color/end" -> events.onNext(Event(CUBES_COLOR_FILL_END, eventMapper.getColor(e)))
                "/cubes/stroke/color" -> events.onNext(Event(CUBES_COLOR_STROKE, eventMapper.getColor(e)))
                "/cubes/stroke/width" -> events.onNext(Event(CUBES_STROKE_WEIGHT, (eventMapper.getFloat(e, 0))))
                "/cubes/stroke/visible" -> events.onNext(Event(CUBES_STROKE, eventMapper.getBoolean(e, 0)))
                "/cubes/scale/base" -> {
                    events.onNext(Event(CUBES_SCALE_BASE, eventMapper.getFloat(e, 0)))
                    events.onNext(Event(CUBES_SCALE_APPLY))
                }
                "/cubes/scale/offset" -> {
                    events.onNext(Event(CUBES_SCALE_OFFSET, eventMapper.getFloat(e, 0)))
                    events.onNext(Event(CUBES_SCALE_APPLY))
                }
                "/cubes/length" -> events.onNext(Event(CUBES_LENGTH, eventMapper.getInt(e, 0)))

                // text
                "/text/stroke/visible" -> events.onNext(Event(TEXT_STROKE, eventMapper.getBoolean(e, 0)))
                "/text/stroke/color" -> events.onNext(Event(TEXT_COLOR_STROKE, eventMapper.getColor(e)))
                "/text/fill/color/start" -> events.onNext(Event(TEXT_COLOR_FILL, eventMapper.getColor(e)))
                "/text/fill/color/end" -> events.onNext(Event(TEXT_COLOR_FILL_END, eventMapper.getColor(e)))
                "/text/fill/alpha" -> events.onNext(Event(TEXT_FILL_ALPHA, eventMapper.getInt0To255(e, 0)))
                "/text/fill" -> events.onNext(Event(TEXT_FILL, eventMapper.getBoolean(e, 0)))
                "/text/font" -> events.onNext(Event(TEXT_FONT, eventMapper.getFont(e)))
                "/text/visible" -> events.onNext(Event(TEXT_VISIBLE, eventMapper.getBoolean(e, 0)))
                "/text/motion" -> events.onNext(Event(TEXT_MOTION, eventMapper.getEnum(e, 0, FADE)))
                "/text/order" -> events.onNext(Event(TEXT_ORDER, eventMapper.getEnum(e, 0, INORDER)))
                "/text/load" -> events.onNext(
                    Event(MENU_OPEN_TEXT, File(files.textDir, eventMapper.getString(e, 0)))
                )
                "/text/next" -> events.onNext(Event(TEXT_NEXT, null))
                "/obj/load" -> events.onNext(Event(ADD_MODEL, eventMapper.getEnum(e, 0, TERMINATOR)))
                "/obj/unload" -> events.onNext(Event(REMOVE_MODEL, eventMapper.getEnum(e, 0, TERMINATOR)))
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



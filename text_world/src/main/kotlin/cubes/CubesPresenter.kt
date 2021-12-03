package cubes

import cubes.CubesContract.Control.*
import cubes.CubesContract.Formation.*
import cubes.CubesContract.Model3D.MILLENIUM_FALCON
import cubes.CubesContract.Model3D.TERMINATOR
import cubes.CubesContract.TextTransition.*
import cubes.gui.Controls
import cubes.models.CubeList
import cubes.models.MilleniumFalcon
import cubes.models.Terminator
import cubes.models.TextList
import cubes.motion.*
import cubes.motion.interpolator.EasingType.IN
import cubes.motion.interpolator.EasingType.OUT
import cubes.motion.interpolator.QuadInterpolator
import cubes.osc.OscContract
import cubes.util.wrapper.FilesWrapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.robmunro.processing.util.ColorUtils.Companion.TRANSPARENT
import processing.core.PVector
import speecher.util.serialization.stateJsonSerializer
import speecher.util.wrapper.logFactory
import java.awt.Color
import java.awt.Font
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class CubesPresenter constructor(
    private val controls: Controls,
    private val view: CubesContract.View,
    private val oscController: OscContract.External,
    private val files: FilesWrapper,
    private val disposables: CompositeDisposable = CompositeDisposable()
) : CubesContract.Presenter {

    val state: CubesState?
        get() = if (this::_state.isInitialized) {
            _state
        } else null

    private lateinit var _state: CubesState

    init {
        oscController.initialise()
    }

    override fun setup() {
        disposables.add(
            controls.events()
                .mergeWith(oscController.events())
                .subscribe({
                    println("receive : ${it.control} : ${it.data} ")
                    when (it.control) {
                        SHADER_BG -> {
                            _state.background = it.data as CubesContract.BackgroundShaderType
                        }
                        BG_COLOR -> {
                            _state.backgroundColor = it.data as Color
                        }
                        MOTION_ANIMATION_TIME -> motionSliderAnimationTime(it.data as Float)
                        CUBES_ROTATION_SPEED -> motionSliderRotationSpeed(it.data as Float)
                        CUBES_ROTATION_OFFEST_SPEED -> motionSliderRotationOffset(it.data as Float)
                        CUBES_ROTATION_OFFEST_RESET -> motionRotationOffsetReset()
                        CUBES_ROTATION ->
                            (it.data as? Pair<CubesContract.RotationAxis, Boolean>)
                                ?.apply { motionRot(first, second) }
                        CUBES_ROTATION_RESET -> motionRotationReset()
                        CUBES_ROTATION_ALIGN -> motionAlignExecute()
                        CUBES_VISIBLE -> cubesVisible(it.data as Boolean)
                        CUBES_FORMATION -> formation(it.data as CubesContract.Formation)
                        CUBES_SCALE_BASE -> motionSliderScale(it.data as Float)
                        CUBES_SCALE_OFFSET -> motionSliderScaleDist(it.data as Float)
                        CUBES_SCALE_APPLY -> motionApplyScale()
                        CUBES_COLOR_FILL_START -> fillColor(it.data as Color)
                        CUBES_COLOR_FILL_END -> fillEndColor(it.data as Color)
                        CUBES_FILL -> fill(it.data as Boolean)
                        CUBES_COLOR_FILL_ALPHA -> fillAlpha(it.data as Int)
                        CUBES_COLOR_STROKE -> strokeColor(it.data as Color)
                        CUBES_STROKE -> stroke(it.data as Boolean)
                        CUBES_STROKE_WEIGHT -> strokeWeight(it.data as Float)
                        CUBES_LENGTH -> cubesLength(it.data as Int)
                        TEXT_ORDER -> textOrder(it.data as TextList.Ordering)
                        TEXT_FONT -> textFont(it.data as Font)
                        TEXT_MOTION -> textMotion(it.data as CubesContract.TextTransition)
                        TEXT_COLOR_FILL -> textFillColor(it.data as Color)
                        TEXT_COLOR_FILL_END -> textFillEndColor(it.data as Color)
                        TEXT_FILL -> textFill(it.data as Boolean)
                        TEXT_FILL_ALPHA -> textFillAlpha(it.data as Int)
                        TEXT_COLOR_STROKE -> textStrokeColor(it.data as Color)
                        TEXT_STROKE_WEIGHT -> textStrokeWeight(it.data as Float)
                        TEXT_STROKE -> textStroke(it.data as Boolean)
                        TEXT_VISIBLE -> textVisible(it.data as Boolean)
                        TEXT_NEXT -> textNext()
                        MENU_SAVE_STATE -> saveState(it.data as File)
                        MENU_OPEN_STATE -> openState(it.data as File)
                        MENU_OPEN_TEXT -> openText(it.data as File)
                        MENU_EXIT -> exit()
                        ADD_MODEL -> addModel(it.data as CubesContract.Model3D)
                        REMOVE_MODEL -> removeModel(it.data as CubesContract.Model3D)
                        else -> println("Couldnt handle : ${it.control} ")
                    }
                }, {
                    println("Exception from UI : ${it.message} ")
                    it.printStackTrace()
                })
        )
        controls.showWindow()

        Completable
            .fromCallable { openState(files.lastStateFile) }
            .delay(10, TimeUnit.SECONDS)
            .subscribe()
    }

    private fun removeModel(model: CubesContract.Model3D) {
        _state.models
            .removeIf { it::class == model.clazz }
    }

    private fun addModel(model: CubesContract.Model3D) {
        Single.fromCallable {
            when (model) {
                TERMINATOR -> Terminator.create(view.getApplet())
                MILLENIUM_FALCON -> MilleniumFalcon.create(view.getApplet())
            }
        }
            .subscribeOn(Schedulers.io())
            .subscribe({
                _state.models.add(it)
            }, {})

    }

    private fun exit() {
        saveState(files.lastStateFile)
        System.exit(0)
    }

    private fun saveState(file: File) {
        stateJsonSerializer
            .encodeToString(CubesState.serializer(), _state)
            .apply { file.writeText(this) }
            .apply { controls.refreshFiles() }
    }

    private fun openState(file: File) {
        if (file.exists()) {
            val json = file.readText()
            setState(stateJsonSerializer
                .decodeFromString(CubesState.serializer(), json)
                .apply {
                    cubeList.apply {
                        setApplet(view.getApplet())
                        cubeListMotion = VelocityRotationMotion.makeCubesRotation(_state)
                    }
                    textList.apply { setApplet(view.getApplet()) }
                    models.forEach { it.setApplet(view.getApplet()) }
                }
            )
        }
    }

    private fun openText(file: File) {
        val list = file.readLines()
        _state.textList = TextList(view.getApplet())
            .apply { list.forEach { addText(it) } }
            .apply { fillColor = Color.YELLOW; visible = true }
    }

    fun updateBeforeDraw() {
        _state.cubeList.updateState()
        _state.models.forEach { it.updateState() }
    }

    private fun cubesLength(i: Int) {
        _state.cubeList = CubeList(view.getApplet(), i, 50f, 50f).apply { visible = true }
    }

    private fun motionSliderRotationSpeed(value: Float) {
        _state.cubesRotationSpeed = value / 10000f
        setCubeVelocity()
    }

    private fun motionSliderRotationOffset(offset: Float) {
        _state.cubesRotationOffset = offset / 10000f
        setCubeVelocity()
    }

    private fun motionRotationReset() {
        _state.cubeList.cubes.forEach { it.angle.set(0f, 0f, 0f) }
    }

    private fun motionRotationOffsetReset() {
        _state.cubesRotationOffset = 0f
        setCubeVelocity()
    }

    private fun strokeWeight(value: Float) {
        //view.setShaderParam(LINES, "weight", value)
        _state.cubeList.cubes.forEach { it.strokeWeight = value }
    }

    private fun motionRot(axis: CubesContract.RotationAxis, selected: Boolean) {
        _state.cubeRotationAxes = when (axis) {
            CubesContract.RotationAxis.X -> _state.cubeRotationAxes.copy(first = selected)
            CubesContract.RotationAxis.Y -> _state.cubeRotationAxes.copy(second = selected)
            CubesContract.RotationAxis.Z -> _state.cubeRotationAxes.copy(third = selected)
        }
        setCubeVelocity()
    }

    private fun motionAlignExecute() {
        _state.cubeList.cubeListMotion = CubeRotationAlignMotion(_state.cubeList, _state.animationTime) {
            _state.cubeList.cubeListMotion = VelocityRotationMotion.makeCubesRotation(_state)
        }.apply { start() }
    }

    private fun motionSliderAnimationTime(alignTime: Float) {
        _state.animationTime = alignTime
    }

    private fun formation(form: CubesContract.Formation) {
        when (form) {
            GRID ->
                _state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.grid(
                                _state.cubeList,
                                _state.animationTime,
                                sqrt(_state.cubeList.length.toDouble()).toInt(),
                                500f
                            ),
                            cubeScaleMotion(),
                            VelocityRotationMotion.makeCubesRotation(_state)
                        )
                    ).apply { start() }
            LINE ->
                _state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.line(_state.cubeList, _state.animationTime, 1000f),
                            cubeScaleMotion(),
                            VelocityRotationMotion.makeCubesRotation(_state)
                        )
                    ).apply { start() }
            CENTER ->
                _state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.zero(_state.cubeList, _state.animationTime),
                            cubeScaleMotion(),
                            VelocityRotationMotion.makeCubesRotation(_state)
                        )
                    ).apply { start() }
            else -> Unit
        }
    }

    private fun motionSliderScale(scale: Float) {
        _state.cubeScale = scale
    }

    private fun motionSliderScaleDist(dist: Float) {
        _state.cubeScaleDist = dist
    }

    private fun fill(selected: Boolean) {
        _state.cubeList.cubes.forEach { it.fill = selected }
    }

    private fun fillColor(color: Color) {
        _state.cubesFillStartColor = Color(color.red, color.green, color.blue, _state.cubesFillAlpha.toInt())
        _state.cubeList.cubes.forEach { it.fillColor = _state.cubesFillStartColor }
    }

    private fun fillEndColor(color: Color) {
        _state.cubesFillEndColor = Color(color.red, color.green, color.blue, _state.cubesFillAlpha.toInt())
        ShapeList.coloriseListGradient(_state.cubeList.cubes, _state.cubesFillStartColor, _state.cubesFillEndColor)
    }

    private fun motionApplyScale() {
        _state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    cubeScaleMotion(),
                    VelocityRotationMotion.makeCubesRotation(_state)
                )
            ).apply { start() }
    }

    private fun strokeColor(color: Color) {
        _state.cubeList.cubes.forEach { it.strokeColor = color }
    }

    private fun stroke(selected: Boolean) {
        _state.cubeList.cubes.forEach { it.stroke = selected }
    }

    private fun fillAlpha(alpha: Int) {
        _state.cubesFillAlpha = alpha.toFloat()
        _state.cubeList.cubes.forEach {
            it.fillColor = Color(it.fillColor.red, it.fillColor.green, it.fillColor.blue, alpha)
        }
    }

    private fun startText(timeMs: Float) {
        _state.textList.apply {
            this.ordering = _state.textOrder
            visible(true)
            this.timeMs = timeMs
            texts.forEach { it.fillColor = TRANSPARENT }
            motion = when (_state.textTransition) {
                FADE -> textColorMotion(timeMs)
                FADE_ZOOM -> CompositeMotion(
                    listOf(
                        textColorMotion(timeMs),
                        textTransitionMotion(timeMs)
                    )
                )
                SPIN -> textRotationMotion(timeMs)
            }.apply { start() }
            endFunction = fun() {
                startText(timeMs)
            }
            start()
        }
    }

    private fun textNext() {
        _state.textList.next()
    }

    private fun TextList.textColorMotion(timeMs: Float): Motion<TextList.Text, Any> {
        val animTimeEdge = (timeMs - 1000f) / 2
        return SeriesMotion(
            listOf(
                TextColorMotion(_state.textList, animTimeEdge, TRANSPARENT, fillColor),
                WaitMotion(1000f),
                TextColorMotion(_state.textList, animTimeEdge, fillColor, TRANSPARENT)
            )
        )
    }

    private fun TextList.textTransitionMotion(timeMs: Float): Motion<TextList.Text, Any> {
        val animTimeEdge = (timeMs - 1000f) / 2
        val startZPos = -1000f
        return SeriesMotion(
            listOf(
                TextTranslationMotion(
                    this, animTimeEdge,
                    target = PVector(0f, 0f, 0f),
                    startPosition = PVector(0f, 0f, startZPos),
                    interp = QuadInterpolator(IN)
                ),
                WaitMotion(1000f),
                TextTranslationMotion(
                    this, animTimeEdge,
                    target = PVector(0f, 0f, startZPos),
                    interp = QuadInterpolator(OUT)
                )
            )
        )
    }

    // fixme : not working
    private fun TextList.textRotationMotion(timeMs: Float): Motion<TextList.Text, Any> {
        val animTimeEdge = (timeMs - 1000f) / 2
        return SeriesMotion(
            listOf(
                TextTranslationMotion(
                    this, 100f,
                    target = PVector(0f, 0f, 0f),
                    interp = QuadInterpolator(IN)
                ),
                TextRotationMotion(
                    this, animTimeEdge,
                    target = PVector((Math.PI * 2).toFloat(), 0f, 0f),
                    interp = QuadInterpolator(IN),
                    log = logFactory(TextTranslationMotion::class.java)
                ),
                WaitMotion(1000f),
                TextRotationMotion(
                    this, animTimeEdge,
                    target = PVector(0f, 0f, 0f),
                    interp = QuadInterpolator(OUT),
                    log = logFactory(TextTranslationMotion::class.java)
                )
            )
        )
    }

    private fun textOrder(order: TextList.Ordering) {
        _state.textOrder = order
        startText(_state.animationTime)
    }

    private fun textMotion(transition: CubesContract.TextTransition) {
        _state.textTransition = transition
        startText(_state.animationTime)
    }

    private fun textFillColor(color: Color) {
        _state.textList.fillColor = color
        _state.textList.texts.forEach {
            it.fillColor = color
        }
    }

    private fun textFillEndColor(color: Color) {

    }

    private fun textFill(selected: Boolean) {
        _state.textList.fill = selected
        _state.textList.texts.forEach {
            it.fill = selected
        }
    }

    private fun textFillAlpha(alpha: Int) {
        val old = _state.textList.fillColor
        _state.textList.fillColor = Color(old.red, old.green, old.blue, alpha)
        _state.textList.texts.forEach {
            val oldt = it.fillColor
            it.fillColor = Color(oldt.red, oldt.green, oldt.blue, alpha)
        }
    }

    private fun textStrokeColor(color: Color) {
        _state.textList.strokeColor = color
        _state.textList.texts.forEach {
            it.strokeColor = color
        }
    }

    private fun textStroke(selected: Boolean) {
        _state.textList.stroke = selected
        _state.textList.texts.forEach {
            it.stroke = selected
        }
    }

    private fun textVisible(selected: Boolean) {
        _state.textList.visible = selected
    }

    private fun textStrokeWeight(weight: Float) {
        _state.textList.strokeWeight = weight
        _state.textList.texts.forEach {
            it.strokeWeight = weight
        }
    }

    private fun textFont(selectedFont: Font) {
        _state.textList.setFont(selectedFont)
    }

    private fun cubesVisible(selected: Boolean) {
        _state.cubeList.visible = selected
    }

    private fun cubeScaleMotion() = if (_state.cubeScaleDist == 0f) {
        CubeScaleMotion.scale(_state.cubeList, _state.animationTime, _state.cubeScale)
    } else {
        CubeScaleMotion.range(_state.cubeList, _state.animationTime, _state.cubeScale, _state.cubeScaleDist)
    }

    override fun setState(state: CubesState) {
        this._state = state
    }

    private fun setCubeVelocity() {
        _state.cubeList.cubeListMotion = VelocityRotationMotion.makeCubesRotation(_state).apply { start() }
    }


}
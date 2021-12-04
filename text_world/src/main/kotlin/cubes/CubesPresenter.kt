package cubes

import cubes.CubesContract.Control.*
import cubes.CubesContract.Formation.*
import cubes.CubesContract.Model3D.MILLENIUM_FALCON
import cubes.CubesContract.Model3D.TERMINATOR
import cubes.CubesContract.TextTransition.*
import cubes.gui.Controls
import cubes.models.*
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
import net.robmunro.processing.util.encodeARGB
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

    private var readyForInput = false

    init {
        oscController.initialise()
    }

    override fun setup() {
        disposables.add(
            controls.events()
                .mergeWith(oscController.events())
                .filter { readyForInput }
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
                        TEXT_FILL_ALPHA -> textFillAlpha(it.data as Int)
                        TEXT_VISIBLE -> textVisible(it.data as Boolean)
                        TEXT_NEXT -> textNext()
                        MENU_SAVE_STATE -> saveState(it.data as File)
                        MENU_OPEN_STATE -> openState(it.data as File)
                        MENU_OPEN_TEXT -> openText(it.data as File)
                        MENU_EXIT -> exit()
                        ADD_MODEL -> addModel(it.data as CubesContract.Model3D)
                        REMOVE_MODEL -> removeModel(it.data as CubesContract.Model3D)
                        ADD_IMAGE -> addImage(it.data as String)
                        REMOVE_IMAGE -> removeImage(it.data as String)
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
            .delay(1, TimeUnit.SECONDS)
            .subscribe({ readyForInput = true }, { it.printStackTrace() })
    }

    private fun addImage(name: String) {
        if (_state.models.find { it::class == SvgImage::class && (it as SvgImage).name == name } == null) {
            _state.models.add(SvgImage.create(view.applet, name))
        }
    }

    private fun removeImage(name: String) {
        _state.models
            .removeIf { it::class == SvgImage::class && (it as SvgImage).name == name }
    }

    private fun removeModel(model: CubesContract.Model3D) {
        _state.models
            .removeIf { it::class == model.clazz }
    }

    private fun addModel(model: CubesContract.Model3D) {
        if (_state.models.find { it::class == model.clazz } == null) {
            Single.fromCallable {
                when (model) {
                    TERMINATOR -> Terminator.create(view.applet)
                    MILLENIUM_FALCON -> MilleniumFalcon.create(view.applet)
                }
            }
                .subscribeOn(Schedulers.io())
                .subscribe({ _state.models.add(it) }, { it.printStackTrace() })
        }
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
                .also { newState ->
                    newState.cubeList
                        .apply { setApplet(view.applet) }
                        .apply { cubeListMotion = VelocityRotationMotion.makeCubesRotation(newState) }
                    newState.textList
                        .apply { setApplet(view.applet) }
                        .apply { fillColor = newState.textColor }
                        .apply { newState.textFont?.also { setFont(it) } }
                    newState.models
                        .forEach { it.setApplet(view.applet) }
                }
            )
            _state.textList.apply { startText() }
        } else {
            setState(CubesState.makeFromState(view.applet))
        }
    }

    private fun openText(file: File) {
        val list = file.readLines()
        _state.textList = TextList(view.applet)
            .apply { list.forEach { addText(it) } }
            .apply { fillColor = _state.textColor }
            .apply { _state.textFont?.also { setFont(it) } }
            .apply { startText() }
    }

    fun updateBeforeDraw() {
        _state.cubeList.updateState()
        _state.models.forEach { it.updateState() }
    }

    private fun cubesLength(i: Int) {
        _state.cubeList = CubeList(view.applet, i, 50f, 50f).apply { visible = true }
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
        // todo override fillCOlor in cubeList
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

    private fun startText() {
        _state.textList.apply {
            this.ordering = _state.textOrder
            visible(true)
            this.timeMs = _state.animationTime
            textMotion = when (_state.textTransition) {
                FADE -> textColorMotion(timeMs)
                FADE_ZOOM -> CompositeMotion(
                    listOf(
                        textColorMotion(timeMs),
                        textTransitionMotion(timeMs)
                    )
                )
                SPIN -> textRotationMotion(timeMs)
                NONE -> null
            }.apply { start() }
            endFunction = fun() {
                startText()
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
        startText()
    }

    private fun textMotion(transition: CubesContract.TextTransition) {
        _state.textTransition = transition
        startText()
    }

    private fun textFillColor(color: Color) {
        println("fillColor: ${color.encodeARGB()}")
        _state.textColor = color
        _state.textList.fillColor = color
    }

    private fun textFillAlpha(alpha: Int) {
        _state.textColor = _state.textColor
            .let { old -> Color(old.red, old.green, old.blue, alpha) }
        println("fillColor: ${_state.textColor}")
        _state.textList.fillColor = _state.textColor
    }

    private fun textVisible(selected: Boolean) {
        _state.textList.visible = selected
    }

    private fun textFont(selectedFont: Font) {
        _state.textFont = selectedFont
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
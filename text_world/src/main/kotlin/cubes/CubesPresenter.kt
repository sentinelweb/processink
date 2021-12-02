package cubes

import cubes.CubesContract.Control.*
import cubes.CubesContract.Formation.*
import cubes.CubesContract.TextTransition.*
import cubes.gui.Controls
import cubes.motion.*
import cubes.motion.interpolator.EasingType.IN
import cubes.motion.interpolator.EasingType.OUT
import cubes.motion.interpolator.QuadInterpolator
import cubes.objects.CubeList
import cubes.objects.TextList
import cubes.objects.TextList.Ordering.RANDOM
import cubes.osc.OscContract
import cubes.util.wrapper.FilesWrapper
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
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

    val cstate: CubesState?
        get() = if (this::state.isInitialized) {
            state
        } else null

    private lateinit var state: CubesState

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
                        SHADER_LINE_NONE -> shaderButtonNone()
                        SHADER_LINE_LINE -> shaderButtonLine()
                        SHADER_LINE_NEON -> shaderButtonNeon()
                        SHADER_BG -> {
                            state.background = it.data as CubesContract.BackgroundShaderType
                        }
                        BG_COLOR -> {
                            state.backgroundColor = it.data as Color
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
                        MENU_SAVE_STATE -> saveState(it.data as File)
                        MENU_OPEN_STATE -> openState(it.data as File)
                        MENU_OPEN_TEXT -> openText(it.data as File)
                        MENU_EXIT -> exit()
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

    private fun exit() {
        saveState(files.lastStateFile)
        System.exit(0)
    }

    private fun saveState(file: File) {
        stateJsonSerializer
            .encodeToString(CubesState.serializer(), state)
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
                        cubeListMotion = VelocityRotationMotion.make(state)
                    }
                    textList.apply { setApplet(view.getApplet()) }
                }
            )
        }
    }

    private fun openText(file: File) {
        val list = file.readLines()
        state.textList = TextList(view.getApplet())
            .apply { list.forEach { addText(it) } }
            .apply { fillColor = Color.YELLOW; visible = true }
    }

    fun updateBeforeDraw() {
        state.cubeList.updateState()
    }

    private fun cubesLength(i: Int) {
        state.cubeList = CubeList(view.getApplet(), i, 50f, 50f).apply { visible = true }
    }

    private fun motionSliderRotationSpeed(value: Float) {
        state.rotationSpeed = value / 10000f
        setCubeVelocity()
    }

    private fun motionSliderRotationOffset(offset: Float) {
        state.rotationOffset = offset / 10000f
        setCubeVelocity()
    }

    private fun motionRotationReset() {
        state.cubeList.cubes.forEach { it.angle.set(0f, 0f, 0f) }
    }

    private fun motionRotationOffsetReset() {
        state.rotationOffset = 0f
        setCubeVelocity()
    }

    private fun shaderButtonNone() {
        //view.setShaderType(CubesContract.ShaderType.NONE)
    }

    private fun shaderButtonLine() {
        //view.setShaderType(LINES)
    }

    private fun shaderButtonNeon() {
        //view.setShaderType(NEON)
    }

    private fun strokeWeight(value: Float) {
        //view.setShaderParam(LINES, "weight", value)
        state.cubeList.cubes.forEach { it.strokeWeight = value }
    }

    private fun motionRot(axis: CubesContract.RotationAxis, selected: Boolean) {
        state.cubeRotationAxes = when (axis) {
            CubesContract.RotationAxis.X -> state.cubeRotationAxes.copy(first = selected)
            CubesContract.RotationAxis.Y -> state.cubeRotationAxes.copy(second = selected)
            CubesContract.RotationAxis.Z -> state.cubeRotationAxes.copy(third = selected)
        }
        setCubeVelocity()
    }

    private fun motionAlignExecute() {
        state.cubeList.cubeListMotion = CubeRotationAlignMotion(state.cubeList, state.animationTime) {
            state.cubeList.cubeListMotion = VelocityRotationMotion.make(state)
        }.apply { start() }
    }

    private fun motionSliderAnimationTime(alignTime: Float) {
        state.animationTime = alignTime
    }

    private fun formation(form: CubesContract.Formation) {
        when (form) {
            GRID ->
                state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.grid(
                                state.cubeList,
                                state.animationTime,
                                sqrt(state.cubeList.length.toDouble()).toInt(),
                                500f
                            ),
                            cubeScaleMotion(),
                            VelocityRotationMotion.make(state)
                        )
                    ).apply { start() }
            LINE ->
                state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.line(state.cubeList, state.animationTime, 1000f),
                            cubeScaleMotion(),
                            VelocityRotationMotion.make(state)
                        )
                    ).apply { start() }
            CENTER ->
                state.cubeList.cubeListMotion =
                    CompositeMotion(
                        listOf(
                            CubeTranslationMotion.zero(state.cubeList, state.animationTime),
                            cubeScaleMotion(),
                            VelocityRotationMotion.make(state)
                        )
                    ).apply { start() }
            else -> Unit
        }
    }

    private fun motionSliderScale(scale: Float) {
        state.cubeScale = scale
    }

    private fun motionSliderScaleDist(dist: Float) {
        state.cubeScaleDist = dist
    }

    private fun fill(selected: Boolean) {
        state.cubeList.cubes.forEach { it.fill = selected }
    }

    private fun fillColor(color: Color) {
        state.fillColor = Color(color.red, color.green, color.blue, state.fillAlpha.toInt())
        state.cubeList.cubes.forEach { it.fillColor = state.fillColor }
    }

    private fun fillEndColor(color: Color) {
        state.fillEndColor = Color(color.red, color.green, color.blue, state.fillAlpha.toInt())
        ShapeList.coloriseListGradient(state.cubeList.cubes, state.fillColor, state.fillEndColor)
    }

    private fun motionApplyScale() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            ).apply { start() }
    }

    private fun strokeColor(color: Color) {
        state.cubeList.cubes.forEach { it.strokeColor = color }
    }

    private fun stroke(selected: Boolean) {
        state.cubeList.cubes.forEach { it.stroke = selected }
    }

    private fun fillAlpha(alpha: Int) {
        state.fillAlpha = alpha.toFloat()
        state.cubeList.cubes.forEach {
            it.fillColor = Color(it.fillColor.red, it.fillColor.green, it.fillColor.blue, alpha)
        }
    }

    private fun textRandom(selected: Boolean) {
        if (selected) {
            state.textOrder = RANDOM
            startText(state.animationTime)
        } else {
            state.textList.visible = false
            state.textList.stop()
        }
    }

    private fun startText(timeMs: Float) {
        state.textList.apply {
            this.ordering = state.textOrder
            visible(true)
            this.timeMs = timeMs
            texts.forEach { it.fillColor = TRANSPARENT }
            motion = when (state.textTransition) {
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

    private fun TextList.textColorMotion(timeMs: Float): Motion<TextList.Text, Any> {
        val animTimeEdge = (timeMs - 1000f) / 2
        return SeriesMotion(
            listOf(
                TextColorMotion(state.textList, animTimeEdge, TRANSPARENT, fillColor),
                WaitMotion(1000f),
                TextColorMotion(state.textList, animTimeEdge, fillColor, TRANSPARENT)
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
                    target = PVector(360f, 0f, 0f),
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

//    private fun textNearRandom(selected: Boolean) {
//        if (selected) {
//            state.textOrder = NEAR_RANDOM
//            startText(state.animationTime)
//        } else {
//            state.textList.visible(false)
//            state.textList.stop()
//        }
//    }

    private fun textOrder(order: TextList.Ordering) {
        state.textOrder = order
        startText(state.animationTime)
    }

    private fun textMotion(transition: CubesContract.TextTransition) {
        state.textTransition = transition
        startText(state.animationTime)
    }


    private fun textFillColor(color: Color) {
        state.textList.fillColor = color
        state.textList.texts.forEach {
            it.fillColor = color
        }
    }

    private fun textFillEndColor(color: Color) {

    }

    private fun textFill(selected: Boolean) {
        state.textList.fill = selected
        state.textList.texts.forEach {
            it.fill = selected
        }
    }

    private fun textFillAlpha(alpha: Int) {
        val old = state.textList.fillColor
        state.textList.fillColor = Color(old.red, old.green, old.blue, alpha)
        state.textList.texts.forEach {
            val oldt = it.fillColor
            it.fillColor = Color(oldt.red, oldt.green, oldt.blue, alpha)
        }
    }

    private fun textStrokeColor(color: Color) {
        state.textList.strokeColor = color
        state.textList.texts.forEach {
            it.strokeColor = color
        }
    }

    private fun textStroke(selected: Boolean) {
        state.textList.stroke = selected
        state.textList.texts.forEach {
            it.stroke = selected
        }
    }

    private fun textVisible(selected: Boolean) {
        state.textList.visible = selected
    }

    private fun textStrokeWeight(weight: Float) {
        state.textList.strokeWeight = weight
        state.textList.texts.forEach {
            it.strokeWeight = weight
        }
    }

    private fun textFont(selectedFont: Font) {
        state.textList.setFont(selectedFont)
    }

    private fun cubesVisible(selected: Boolean) {
        state.cubeList.visible = selected
    }

    private fun cubeScaleMotion() = if (state.cubeScaleDist == 0f) {
        CubeScaleMotion.scale(state.cubeList, state.animationTime, state.cubeScale)
    } else {
        CubeScaleMotion.range(state.cubeList, state.animationTime, state.cubeScale, state.cubeScaleDist)
    }

    override fun setState(state: CubesState) {
        this.state = state
    }

    private fun setCubeVelocity() {
        state.cubeList.cubeListMotion = VelocityRotationMotion.make(state).apply { start() }
    }


}
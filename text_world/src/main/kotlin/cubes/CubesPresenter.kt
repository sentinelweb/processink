package cubes

import cubes.CubesContract.BackgroundShaderType.*
import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls
import cubes.gui.Controls.UiObject.*
import cubes.motion.*
import cubes.objects.TextList
import cubes.objects.TextList.Ordering.*
import io.reactivex.disposables.CompositeDisposable
import processing.core.PVector
import java.awt.Color
import java.awt.Font

class CubesPresenter constructor(
    private val controls: Controls,
    private val view: CubesContract.View,
    private val disposables: CompositeDisposable = CompositeDisposable()
) : CubesContract.Presenter {

    private lateinit var state: CubesState

    override fun setup() {

        disposables.add(
            controls.events().subscribe({
                println("receive : ${it.uiObject} : ${it.data} ")
                when (it.uiObject) {
                    SHADER_LINE_NONE -> shaderButtonNone()
                    SHADER_LINE_LINE -> shaderButtonLine()
                    SHADER_LINE_NEON -> shaderButtonNeon()
                    SHADER_BG_NEBULA -> shaderButtonNebula()
                    SHADER_BG_FLAME -> shaderButtonColdFlame()
                    SHADER_BG_REFRACT -> shaderButtonRefraction()
                    MOTION_ANIMATION_TIME -> motionSliderAnimationTime(it.data as Float)
                    CUBES_ROTATION_SLIDER -> motionSliderRotationSpeed(it.data as Float)
                    CUBES_ROTATION_OFFEST_SLIDER -> motionSliderRotationOffset(it.data as Float)
                    CUBES_ROTATION_OFFEST_RESET -> motionRotationOffsetReset()
                    CUBES_ROTATION_X -> motionRotX(it.data as Boolean)
                    CUBES_ROTATION_Y -> motionRotY(it.data as Boolean)
                    CUBES_ROTATION_Z -> motionRotZ(it.data as Boolean)
                    CUBES_ROTATION_RESET -> motionRotationReset()
                    CUBES_ROTATION_ALIGN -> motionAlignExecute()
                    CUBES_VISIBLE -> cubesVisible(it.data as Boolean)
                    CUBES_GRID -> motionGrid()
                    CUBES_LINE -> motionLine()
                    CUBES_SQUARE -> motionSquare()
                    CUBES_TRANSLATION_RESET -> motionTranslationReset()
                    CUBES_SCALE_BASE_SLIDER -> motionSliderScale(it.data as Float)
                    CUBES_SCALE_OFFSET_SLIDER -> motionSliderScaleDist(it.data as Float)
                    CUBES_SCALE_APPLY -> motionApplyScale()
                    CUBES_COLOR_FILL_START -> fillColor(it.data as Color)
                    CUBES_COLOR_FILL_END -> fillEndColor(it.data as Color)
                    CUBES_FILL -> fill(it.data as Boolean)
                    CUBES_COLOR_FILL_ALPHA -> fillAlpha(it.data as Float)
                    CUBES_COLOR_STROKE -> strokeColor(it.data as Color)
                    CUBES_STROKE -> stroke(it.data as Boolean)
                    CUBES_STROKE_WEIGHT -> strokeWeight(it.data as Float)
                    TEXT_ORDER_RANDOM -> textRandom(it.data as Boolean)
                    TEXT_ORDER_NEAR_RANDOM -> textNearRandom(it.data as Boolean)
                    TEXT_ORDER_INORDER -> textInOrder(it.data as Boolean)
                    TEXT_FONT -> textFont(it.data as Font)
                    TEXT_MOTION_CUBE -> textMotionCube(it.data as Boolean)
                    TEXT_MOTION_AROUND -> textMotionAround(it.data as Boolean)
                    TEXT_MOTION_FADE -> textMotionFade(it.data as Boolean)
                    TEXT_COLOR_FILL -> textFillColor(it.data as Color)
                    TEXT_COLOR_FILL_END -> textFillEndColor(it.data as Color)
                    TEXT_FILL -> textFill(it.data as Boolean)
                    TEXT_FILL_ALPHA -> textFillAlpha(it.data as Float)
                    TEXT_COLOR_STROKE -> textStrokeColor(it.data as Color)
                    TEXT_STROKE_WEIGHT -> textStrokeWeight(it.data as Float)
                    TEXT_STROKE -> textStroke(it.data as Boolean)
                    else -> println("Couldnt handle : ${it.uiObject} ")
                }
            }, {
                println("Exception from UI : ${it.message} ")
                it.printStackTrace()
            })
        )
        controls
            .showWindow()
    }

    fun updateBeforeDraw() {
        state.cubeList.updateState()
    }

    fun motionSliderRotationSpeed(value: Float) {
        state.rotationSpeed = value / 10000f
        setCubeVelocity()
    }


    fun motionSliderRotationOffset(offset: Float) {
        state.rotationOffset = offset / 10000f
        setCubeVelocity()
    }

    fun motionRotationReset() {
        state.cubeList.cubes.forEach { it.angle.set(0f, 0f, 0f) }
    }

    fun motionRotationOffsetReset() {
        state.rotationOffset = 0f
        setCubeVelocity()
    }

    fun shaderButtonNone() {
        view.setShaderType(NONE)
    }

    fun shaderButtonLine() {
        view.setShaderType(LINES)
    }

    fun shaderButtonNeon() {
        view.setShaderType(NEON)
    }

    fun shaderButtonNebula() {
        view.setBackgroundShaderType(NEBULA)
    }

    fun shaderButtonColdFlame() {
        view.setBackgroundShaderType(COLDFLAME)
    }

    fun shaderButtonRefraction() {
        view.setBackgroundShaderType(REFRACTION_PATTERN)
    }

    fun strokeWeight(value: Float) {
        view.setShaderParam(LINES, "weight", value)
        state.cubeList.cubes.forEach { it.strokeWeight = value }
    }

    fun motionRotX(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(first = selected)
        setCubeVelocity()
    }

    fun motionRotY(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(second = selected)
        setCubeVelocity()
    }

    fun motionRotZ(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(third = selected)
        setCubeVelocity()
    }

    fun motionAlignExecute() {
        state.cubeList.cubeListMotion = CubeRotationAlignMotion(state.cubeList, state.animationTime) {
            state.cubeList.cubeListMotion = VelocityRotationMotion.make(state)
        }
    }

    fun motionSliderAnimationTime(alignTime: Float) {
        state.animationTime = alignTime
    }

    fun motionGrid() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.grid(state.cubeList, state.animationTime, 4, 200f),
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
        state.cubeList.cubeListMotion.start()
    }

    fun motionLine() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.line(state.cubeList, state.animationTime, 1000f),
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
        state.cubeList.cubeListMotion.start()
    }

    fun motionSquare() {

    }

    fun motionTranslationReset() {
        state.cubeList.cubes.forEach { it.position.set(0f, 0f, 0f) }
    }

    fun motionSliderScale(scale: Float) {
        state.cubeScale = scale
    }

    fun motionSliderScaleDist(dist: Float) {
        state.cubeScaleDist = dist
    }

    fun fill(selected: Boolean) {
        state.cubeList.cubes.forEach { it.fill = selected }
    }

    fun fillColor(color: Color) {
        state.fillColor = Color(color.red, color.green, color.blue, state.fillAlpha.toInt())
        state.cubeList.cubes.forEach { it.fillColor = state.fillColor }
    }

    fun fillEndColor(color: Color) {
        state.fillEndColor = Color(color.red, color.green, color.blue, state.fillAlpha.toInt())
        ShapeList.coloriseListGradient(state.cubeList.cubes, state.fillColor, state.fillEndColor)
    }

    fun motionApplyScale() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
        state.cubeList.cubeListMotion.start()
    }

    fun strokeColor(color: Color) {
        state.cubeList.cubes.forEach { it.strokeColor = color }
    }

    fun stroke(selected: Boolean) {
        state.cubeList.cubes.forEach { it.stroke = selected }
    }

    fun fillAlpha(alpha: Float) {
        state.fillAlpha = alpha
        state.cubeList.cubes.forEach {
            it.fillColor = Color(it.fillColor.red, it.fillColor.green, it.fillColor.blue, alpha.toInt())
        }
    }

    fun textRandom(selected: Boolean) {
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
            val motionToUse: Motion<TextList.Text, Any> = when (state.textTransition) {
                TextTransition.FADE -> textColorMotion(timeMs)
                TextTransition.FADE_ZOOM -> CompositeMotion(
                    listOf(
                        textColorMotion(timeMs),
                        textTransitionMotion(timeMs)
                    )
                )
                TextTransition.ZOOM -> textTransitionMotion(timeMs)
            }
            motion = motionToUse
            motionToUse.start()
            endFunction = fun() {
                startText(timeMs)
            }
            start()
        }
    }

    private fun TextList.textColorMotion(timeMs: Float): Motion<TextList.Text, Any> {
        return SeriesMotion(
            listOf(
                TextColorMotion(state.textList, timeMs / 2, TRANSPARENT, fillColor),
                TextColorMotion(state.textList, timeMs / 2, fillColor, TRANSPARENT)
            )
        )
    }

    private fun TextList.textTransitionMotion(timeMs: Float): Motion<TextList.Text, Any> {
        return SeriesMotion(
            listOf(
                TextTranslationMotion(this, timeMs / 2, this.texts.map { PVector(0f, 0f, 0f) }),
                TextTranslationMotion(this, timeMs / 2, this.texts.map { PVector(0f, 0f, -10000f) })
            )
        )
    }

    fun textNearRandom(selected: Boolean) {
        if (selected) {
            state.textOrder = NEAR_RANDOM
            startText(state.animationTime)
        } else {
            state.textList.visible(false)
            state.textList.stop()
        }
    }

    fun textInOrder(selected: Boolean) {
        if (selected) {
            state.textOrder = INORDER
        } else {
            state.textOrder = REVERSE
        }
        startText(state.animationTime)
    }

    fun textMotionCube(selected: Boolean) {
        state.textTransition = TextTransition.ZOOM
        startText(state.animationTime)
    }

    fun textMotionAround(selected: Boolean) {
        state.textTransition = TextTransition.FADE_ZOOM
        startText(state.animationTime)
    }

    fun textMotionFade(selected: Boolean) {
        state.textTransition = TextTransition.FADE
        startText(state.animationTime)
    }

    fun textFillColor(color: Color) {
        state.textList.fillColor = color
        state.textList.texts.forEach {
            it.fillColor = color
        }
    }

    fun textFillEndColor(color: Color) {

    }

    fun textFill(selected: Boolean) {
        state.textList.fill = selected
        state.textList.texts.forEach {
            it.fill = selected
        }
    }

    fun textFillAlpha(alpha: Float) {
        val old = state.textList.fillColor
        state.textList.fillColor = Color(old.red, old.green, old.blue, alpha.toInt())
        state.textList.texts.forEach {
            val oldt = it.fillColor
            it.fillColor = Color(oldt.red, oldt.green, oldt.blue, alpha.toInt())
        }
    }

    fun textStrokeColor(color: Color) {
        state.textList.strokeColor = color
        state.textList.texts.forEach {
            it.strokeColor = color
        }
    }

    fun textStroke(selected: Boolean) {
        state.textList.stroke = selected
        state.textList.texts.forEach {
            it.stroke = selected
        }
    }

    fun textStrokeWeight(weight: Float) {
        state.textList.strokeWeight = weight
        state.textList.texts.forEach {
            it.strokeWeight = weight
        }
    }

    fun textFont(selectedFont: Font) {
        state.textList.setFont(selectedFont)
    }

    fun cubesVisible(selected: Boolean) {
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
        state.cubeList.cubeListMotion = VelocityRotationMotion.make(state)
    }

    companion object {
        private val TRANSPARENT = Color(0f, 0f, 0f, 0f)
    }
}
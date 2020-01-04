package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls
import cubes.motion.*
import java.awt.Color
import java.awt.Color.WHITE
import java.awt.Font

class CubesPresenter constructor(
    private val controls: Controls,
    private val view: CubesContract.View
) : CubesContract.Presenter, Controls.Listener {

    private lateinit var state: CubesState

    override fun setup() {
        controls
            .setListener(this)
            .showWindow()
    }

    fun updateBeforeDraw() {
        state.cubeList.updateState()
    }

    override fun motionSliderRotationSpeed(value: Float) {
        state.rotationSpeed = value / 10000f
        setCubeVelocity()
    }

    override fun motionRotationReset() {
        state.cubeList.cubes.forEach { it.angle.set(0f, 0f, 0f) }
    }

    override fun shaderButtonNone() {
        view.setShaderType(NONE)
    }

    override fun shaderButtonLine() {
        view.setShaderType(LINES)
    }

    override fun shaderButtonNeon() {
        view.setShaderType(NEON)
    }

    override fun strokeWeight(value: Float) {
        view.setShaderParam(LINES, "weight", value)
        state.cubeList.cubes.forEach { it.strokeWeight = value }
    }

    override fun motionRotX(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(first = selected)
        setCubeVelocity()
    }

    override fun motionRotY(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(second = selected)
        setCubeVelocity()
    }

    override fun motionRotZ(selected: Boolean) {
        state.cubeRotationAxes = state.cubeRotationAxes.copy(third = selected)
        setCubeVelocity()
    }

    override fun motionAlignExecute() {
        state.cubeList.cubeListMotion = CubeRotationAlignMotion(state.cubeList, state.animationTime) {
            state.cubeList.cubeListMotion = VelocityRotationMotion.make(state)
        }
    }

    override fun motionSliderAnimationTime(alignTime: Float) {
        state.animationTime = alignTime
    }

    override fun motionSliderRotationOffset(offset: Float) {
        state.rotationOffset = offset / 10000f
        setCubeVelocity()
    }

    override fun motionGrid() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.grid(state.cubeList, state.animationTime, 4, 200f),
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
    }

    override fun motionLine() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.line(state.cubeList, state.animationTime, 1000f),
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
    }

    override fun motionSquare() {

    }

    override fun motionTranslationReset() {
        state.cubeList.cubes.forEach { it.position.set(0f, 0f, 0f) }
    }

    override fun motionSliderScale(scale: Float) {
        state.cubeScale = scale
    }

    override fun motionSliderScaleDist(dist: Float) {
        state.cubeScaleDist = dist
    }

    override fun fillColor(color: Color) {
        state.fillColor = color
        state.cubeList.cubes.forEach { it.fillColor = color }
    }

    override fun motionApplyScale() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    cubeScaleMotion(),
                    VelocityRotationMotion.make(state)
                )
            )
    }

    override fun fill(selected: Boolean) {
        state.cubeList.cubes.forEach { it.fill = selected }
    }

    override fun fillEndColor(color: Color) {
        state.fillEndColor = color
        ShapeList.coloriseListGradient(state.cubeList.cubes, state.fillColor, state.fillEndColor)
    }

    override fun strokeColor(color: Color) {
        state.cubeList.cubes.forEach { it.strokeColor = color }
    }

    override fun stroke(selected: Boolean) {
        state.cubeList.cubes.forEach { it.stroke = selected }
    }

    override fun fillAlpha(alpha: Float) {
        state.cubeList.cubes.forEach {
            it.fillColor = Color(it.fillColor.red, it.fillColor.green, it.fillColor.blue, alpha.toInt())
        }
    }

    override fun motionRotationOffsetReset() {

    }

    override fun textRandom(selected: Boolean) {
        if (selected) {
            state.textList.scatterText(-500f, 300f)
            state.textList.fill(true)
            state.textList.visible = true
            state.textList.motion = TextColorMotion(
                state.textList, 2000f, Color(1f, 0f, 0f, 0f), WHITE
            )
        } else {
            state.textList.visible = false
        }
    }

    override fun textNearRandom(selected: Boolean) {

    }

    override fun textInOrder(selected: Boolean) {

    }

    override fun textMotionCube(selected: Boolean) {

    }

    override fun textMotionAround(selected: Boolean) {

    }

    override fun textMotionFade(selected: Boolean) {

    }

    override fun textFillColor(color: Color) {

    }

    override fun textFillEndColor(color: Color) {

    }

    override fun textFill(selected: Boolean) {

    }

    override fun textFillAlpha(alpha: Float) {

    }

    override fun textStrokeColor(color: Color) {

    }

    override fun textStroke(selected: Boolean) {

    }

    override fun textStrokeWeight(weight: Float) {

    }

    override fun textFont(selectedFont: Font) {

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
}
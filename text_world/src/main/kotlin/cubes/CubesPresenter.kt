package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls
import cubes.motion.*
import processing.core.PVector
import java.awt.Color

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
        state.cubeList.cubes.forEach { it.angle = PVector(0f, 0f, 0f) }
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
        state.cubeList.cubes.forEachIndexed { i, cube ->
            cube.fillColor = Color(
                Motion.interpolate(
                    state.fillColor.red.toFloat(),
                    state.fillEndColor.red.toFloat(),
                    i.toFloat() / state.cubeList.cubes.size
                ).toInt(),
                Motion.interpolate(
                    state.fillColor.green.toFloat(),
                    state.fillEndColor.green.toFloat(),
                    i.toFloat() / state.cubeList.cubes.size
                ).toInt(),
                Motion.interpolate(
                    state.fillColor.blue.toFloat(),
                    state.fillEndColor.blue.toFloat(),
                    i.toFloat() / state.cubeList.cubes.size
                ).toInt()
            )
        }
    }

    override fun strokeColor(color: Color) {
        state.cubeList.cubes.forEach { it.strokeColor = color }
    }

    override fun stroke(selected: Boolean) {
        state.cubeList.cubes.forEach { it.stroke = selected }
    }

    override fun fillAlpha(alpha: Float) {
        state.cubeList.cubes.forEach { it.fillAlpha = alpha }
    }

    override fun textRandom(selected: Boolean) {

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
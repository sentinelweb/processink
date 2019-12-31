package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls
import cubes.motion.*
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

    override fun motionSliderSpeed(value: Float) {
        state.rotationSpeed = value / 10000f
        setCubeVelocity()

    }

    override fun motionRotationReset() {
        state.cubeList.cubes.forEach { it.angle = Triple(0f, 0f, 0f) }
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
            state.cubeList.cubeListMotion = VelocityRotationMotion(0f, state.cubeRotationAxes)
        }
    }

    override fun motionSliderRotationTime(alignTime: Float) {
        state.animationTime = alignTime
    }

    override fun motionGrid() {
        val dimension = Math.sqrt(state.cubeList.cubes.size.toDouble()).toInt() + 1
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.grid(state.cubeList, state.animationTime, dimension, 200f),
                    cubeScaleMotion(),
                    VelocityRotationMotion(state.rotationSpeed, state.cubeRotationAxes)
                )
            )
    }

    override fun motionLine() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    CubeTranslationMotion.line(state.cubeList, state.animationTime, 50f),
                    cubeScaleMotion(),
                    VelocityRotationMotion(state.rotationSpeed, state.cubeRotationAxes)
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
        state.cubeList.cubes.forEach { it.fillColor = color;  }
    }

    override fun motionApplyScale() {
        state.cubeList.cubeListMotion =
            CompositeMotion(
                listOf(
                    cubeScaleMotion(),
                    VelocityRotationMotion(state.rotationSpeed, state.cubeRotationAxes)
                )
            )
    }

    override fun fill(selected: Boolean) {
        state.cubeList.cubes.forEach { it.fill = selected; }
    }

    override fun fillEndColor(color: Color) {

    }

    override fun strokeColor(color: Color) {

    }

    override fun stroke(selected: Boolean) {

    }

    override fun fillAlpha(alpha: Float) {

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
        state.cubeList.cubeListMotion = VelocityRotationMotion(state.rotationSpeed, state.cubeRotationAxes)
    }
}
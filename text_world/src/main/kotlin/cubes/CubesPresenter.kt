package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls
import cubes.motion.CubeRotationAlignMotion
import cubes.motion.VelocityRotationMotion

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

    override fun motionResetRotation() {
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

    override fun shaderSliderWeight(value: Float) {
        view.setShaderParam(LINES, "weight", value)
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
        state.cubeList.stateUpdater = CubeRotationAlignMotion(state.cubeList, state.cubeAlignTime) {
            state.cubeList.stateUpdater = VelocityRotationMotion(0f, state.cubeRotationAxes)
        }
    }

    override fun motionSliderAlignTime(alignTime: Float) {
        state.cubeAlignTime = alignTime
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

    override fun setState(state: CubesState) {
        this.state = state
    }

    private fun setCubeVelocity() {
        state.cubeList.stateUpdater = VelocityRotationMotion(state.rotationSpeed, state.cubeRotationAxes)
    }
}

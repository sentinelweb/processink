package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls

class CubesPresenter constructor(
    private val controls: Controls,
    private val view: CubesContract.View
) : CubesContract.Presenter, Controls.Listener {

    private lateinit var state : CubesState

    private var motionRotation = Triple(true, true, true)

    init {
        controls
            .setListener(this)
            .showWindow()
    }

    override fun motionSliderSpeed(value: Float) {
        val rotationSpeed = value / 10000f
        state.cubeList.stateUpdater = fun(i: Int, cube: Cube) {
            val fl = rotationSpeed * (i + 1)
            cube.angle = Triple(
                cube.angle.first + if (motionRotation.first) fl else 0f,
                cube.angle.second + if (motionRotation.second) fl else 0f,
                cube.angle.third + if (motionRotation.third) fl else 0f
            )
        }
    }

    override fun motionResetRotation() {
        state.cubeList.cubes.forEach{it.angle = Triple(0f,0f,0f) }
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

    override fun motionRotZ(selected: Boolean) {
        motionRotation = motionRotation.copy(third = selected)
    }

    override fun motionRotY(selected: Boolean) {
        motionRotation = motionRotation.copy(second = selected)
    }

    override fun motionRotX(selected: Boolean) {
        motionRotation = motionRotation.copy(first = selected)
    }

    override fun motionAlignExecute() {

    }

    override fun motionSliderAlignTime(alignTime: Float) {

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

}
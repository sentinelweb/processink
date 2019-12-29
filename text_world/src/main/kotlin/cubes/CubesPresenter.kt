package cubes

import cubes.CubesContract.ShaderType.*
import cubes.gui.Controls

class CubesPresenter constructor(
    private val controls: Controls,
    private val cubeApplet: CubesContract.View
) : CubesContract.Presenter, Controls.Listener {

    init {
        controls
            .setListener(this)
            .showWindow()
    }

    override fun motionSliderSpeed(value: Float) {
        val rotationSpeed = value / 10000f
        cubeApplet.setCubesMotion(fun(i: Int, cube: Cube) {
            cube.angle += rotationSpeed * (i + 1)
        })
    }

    override fun shaderButtonNone() {
        cubeApplet.setShaderType(NONE)
    }

    override fun shaderButtonLine() {
        cubeApplet.setShaderType(LINES)
    }

    override fun shaderButtonNeon() {
        cubeApplet.setShaderType(NEON)
    }

    override fun shaderSliderWeight(value: Float) {
        cubeApplet.setShaderParam(LINES, "weight", value)
    }

    override fun motionRotZ(selected: Boolean) {

    }

    override fun motionRotY(selected: Boolean) {

    }

    override fun motionRotX(selected: Boolean) {

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

}
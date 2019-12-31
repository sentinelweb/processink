package cubes

import cubes.CubesContract.ShaderType
import cubes.TextList.Text
import cubes.gui.Controls
import cubes.shaders.FlameShader
import cubes.shaders.LineShader
import cubes.shaders.ShaderWrapper
import processing.core.PApplet
import processing.core.PConstants
import java.awt.Color

fun main() {
    val cubes = CubesProcessingView()
    val controls = Controls()
    val presenter = CubesPresenter(controls, cubes)
    cubes.cubesPresenter = presenter
    cubes.run()
}

class CubesProcessingView : PApplet(), CubesContract.View {
    private lateinit var lineShader: LineShader
    private lateinit var flameShader: FlameShader
    private var currentShader: ShaderWrapper? = null
    //lateinit var terminator:Terminator
    lateinit var cubesPresenter: CubesPresenter
    lateinit var cubesState: CubesState

    var color = Color.BLACK

    fun getInfo() = PAppletInfo(width, height)

    override fun settings() {
        size(1280, 720, PConstants.P3D)
    }

    override fun setup() {
        //terminator = Terminator(this)
        cubesState = CubesState(
            textList = TextList(
                this, mutableListOf(
                    Text("In every fact"),
                    Text("there is something"),
                    Text("that is true and false."),
                    Text("Every fact is true and false"),
                    Text("at the same time."),
                    Text("The truth is resonance."),
                    Text("All truth has a context"),
                    Text("and that context is us."),
                    Text("But this is at odds"),
                    Text("with the very definition of truth."),
                    Text("That truth is universal."),
                    Text("All truth is yours"),
                    Text("and don't let anyone "),
                    Text("tell you differently."),
                    Text("Love without hope.")
                )
            ),
            cubeList = CubeList(this, 15, 50f, 400f),
            rotationSpeed = 0.001f,
            animationTime = 2000f,
            info = getInfo(),
            cubeScale = 10f,
            cubeScaleDist = 0f
        )
        cubesPresenter.setState(cubesState)
        lineShader = LineShader(this)
        lineShader.set("weight", 5f)
        flameShader = FlameShader(this)
        hint(PConstants.DISABLE_DEPTH_MASK)
        //currentShader = lineShader
        cubesPresenter.setup()
    }

    // make a algo to send different cubes to catch each other up.
    override fun draw() {
        cubesPresenter.updateBeforeDraw()
        currentShader?.engage() ?: resetShader()
        background(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
        // alignTexts()
        cubesState.cubeList.draw()

        //terminator.draw()
        //textList.draw()
        //text("Love without hope", 320f, 180f)
    }

    override fun setShaderType(type: ShaderType) {
        when (type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> currentShader = lineShader
            ShaderType.NEON -> currentShader = flameShader
        }
    }

    override fun setShaderParam(type: ShaderType, param: String, value: Any) {
        when (type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> lineShader.set(param, value)
            ShaderType.NEON -> flameShader.set(param, value)
        }
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"
    }

    /// experiments //////////////
//    val textToCube = { i: Int, cube: Cube ->
//        val angleWrap = cube.angle % 2 * Math.PI
//        val threshold = 1
//        if (angleWrap < threshold || angleWrap > 2 * Math.PI - threshold) {
//            pushMatrix()
//            rotateX(cube.angle)
//            rotateY(cube.angle)
//            textList.texts[i].draw(this)
//            popMatrix()
//        }
//    }

//    private fun alignTexts() {
//        textList.apply { setProps() }
//            .texts
//            .zip(cubesList.cubes.toTypedArray())
//            .forEach { (text, cube) ->
//                val fl = cube.width / 2
//                text.point.set(fl - textWidth(text.text.toString()) / 2, fl, fl)
//            }
//    }
}
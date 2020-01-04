package cubes

import cubes.CubesContract.ShaderType
import cubes.gui.Controls
import cubes.objects.CubeList
import cubes.objects.TextList
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

    private var textVisible = false

    lateinit var cubesState: CubesState

    var color = Color.BLACK

    fun getInfo() = PAppletInfo(width, height)

    override fun settings() {
        size(1280, 720, PConstants.P3D)
    }

    override fun setup() {
        //terminator = Terminator(this)
        val textList = TextList(this)
            .addText("In every fact")
            .addText("there is something")
            .addText("that is true and false.")
            .addText("Every fact is true and false")
            .addText("at the same time.")
            .addText("The truth is resonance.")
            .addText("All truth has a context")
            .addText("and that context is us.")
            .addText("But this is at odds")
            .addText("with the very definition of truth.")
            .addText("That truth is universal.")
            .addText("All truth is yours")
            .addText("and yours alone")
            .addText("and don't let anyone ")
            .addText("tell you differently.")
            .addText("Love without hope.")

        cubesState = CubesState(
            textList = textList,
            cubeList = CubeList(this, textList.texts.size, 50f, 400f),
            rotationSpeed = 0.001f,
            animationTime = 2000f,
            info = getInfo(),
            cubeScale = 10f,
            cubeScaleDist = 0f,
            rotationOffset = 0f,
            fillColor = Color.WHITE,
            fillEndColor = Color.GRAY
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
        if (textVisible) {
            cubesState.textList.draw()
        }
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

    override fun setTextVisible(visible: Boolean) {
        textVisible = visible
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
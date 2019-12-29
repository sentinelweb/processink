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
    val cubes = Cubes()
    val controls = Controls()
    val presenter = CubesPresenter(controls, cubes)
    cubes.cubesPresenter = presenter
    cubes.run()
}

class Cubes : PApplet(), CubesContract.View {
    private lateinit var lineShader: LineShader
    private lateinit var flameShader: FlameShader
    private lateinit var cubesList: CubeList
    private lateinit var textList: TextList
    private var currentShader:ShaderWrapper? = null
    //lateinit var terminator:Terminator
    lateinit var cubesPresenter: CubesPresenter

    var color = Color.BLACK

    override fun settings() {
        size(1280, 720, PConstants.P3D)
    }

    override fun setup() {
        //terminator = Terminator(this)
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
        )
        lineShader = LineShader(this)
        lineShader.set("weight", 20f)
        flameShader = FlameShader(this)
        cubesList = CubeList(this, textList.texts.size, 50f, 400f)
        hint(PConstants.DISABLE_DEPTH_MASK)
        currentShader = flameShader
    }

    // make a algo to send different cubes to catch each other up.
    override fun draw() {
        currentShader?.engage() ?: resetShader()
        background(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
        // alignTexts()
        cubesList.draw()

        //terminator.draw()
        //textList.draw()
        //text("Love without hope", 320f, 180f)
    }

    override fun setShaderType(type: ShaderType) {
        when(type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> currentShader = lineShader
            ShaderType.NEON -> currentShader = flameShader
        }
    }

    override fun setShaderParam(type: ShaderType, param: String, value: Any) {
        when(type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> lineShader.set(param,value)
            ShaderType.NEON -> flameShader.set(param,value)
        }
    }

    override fun setCubesMotion(function: (Int, Cube) -> Unit) {
        cubesList.stateUpdater = function
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
package cubes

import cubes.TextList.Text
import cubes.gui.Controls
import cubes.gui.SwingGui
import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader
import java.awt.Color

fun main(args: Array<String>) {
    val cubes = Cubes()
    cubes.run()
    //SwingGui(cubes).show()
    Controls(cubes)
}

class Cubes : PApplet(), Controls.MyPanel.Listener {
    private lateinit var lineShader: PShader
    private lateinit var cubesList: CubeList
    private lateinit var textList: TextList
    private var currentShader:PShader? = null
    //lateinit var terminator:Terminator

    var color = Color.BLACK

    private var rotationSpeed = 0.01f

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
        lineShader = loadShader(
            "$BASE_RESOURCES/cubes/linefrag.glsl",
            "$BASE_RESOURCES/cubes/linevert.glsl"
        )
        lineShader.set("weight", 20f)
        cubesList = CubeList(this, textList.texts.size, 50f, 400f)
        hint(PConstants.DISABLE_DEPTH_MASK)
        //currentShader = lineShader
    }

    private fun alignTexts() {
        textList.apply { setProps() }
            .texts
            .zip(cubesList.cubes.toTypedArray())
            .forEach { (text, cube) ->
                val fl = cube.width / 2
                text.point.set(fl - textWidth(text.text.toString()) / 2, fl, fl)

//                val fl = cube.width * Math.sqrt(2.0).toFloat()
//                val angle = cube.angle + Math.PI / 4
//                text.point.set(fl*Math.sin(angle).toFloat(), fl*Math.sin(angle).toFloat() , fl*Math.sin(angle).toFloat() )
            }
    }

    // make a algo to send different cubes to catch each other up.
    override fun draw() {
        currentShader?.let { shader(lineShader, PConstants.LINES) } ?: resetShader()
        background(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
        alignTexts()
        cubesList.draw()

        //terminator.draw()
        //textList.draw()
        //text("Love without hope", 320f, 180f)
    }

    override fun sliderSpeed(value: Float) {
        rotationSpeed = value / 10000f
        cubesList.stateUpdater = fun(i: Int, cube: Cube) {
            cube.angle += rotationSpeed * (i + 1)
            // cube.angle += 0.001f + if (i>0) cubes[i-1].angle * 0.01f else 0f
        }
    }

    override fun slider2(value: Float) {

    }

    override fun slider3(value: Float) {

    }

    override fun buttonNone() {
        currentShader = null
    }

    override fun buttonLine() {
        currentShader = lineShader
    }

    override fun buttonNeon() {

    }

    override fun sliderWeight(value: Float) {
        lineShader.set("weight", value)
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"

    }

    /// experiments //////////////
    val textToCube = { i: Int, cube: Cube ->
        val angleWrap = cube.angle % 2 * Math.PI
        val threshold = 1
        if (angleWrap < threshold || angleWrap > 2 * Math.PI - threshold) {
            pushMatrix()
            rotateX(cube.angle)
            rotateY(cube.angle)
            textList.texts[i].draw(this)
            popMatrix()
        }
    }
}
package cubes

import cubes.TextList.Text
import cubes.gui.SwingGui
import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader
import java.awt.Color

fun main(args: Array<String>) {
    val cubes = Cubes()
    cubes.run()
    SwingGui(cubes).show()
}

class Cubes : PApplet() {
    private lateinit var lineShader: PShader
    private lateinit var cubesList: CubeList
    private lateinit var textList: TextList
    //lateinit var terminator:Terminator

    var color = Color.BLACK

    override fun settings() {
        size(1920, 1080, PConstants.P3D)
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
        cubesList = CubeList(this, textList.texts.size, 50f, 500f)
        hint(PConstants.DISABLE_DEPTH_MASK)
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
        background(color.red.toFloat(),color.green.toFloat(),color.blue.toFloat())
        alignTexts()
        lineShader.set("weight", mouseX / 200f)
        shader(lineShader, PConstants.LINES)
        cubesList.draw() { i, cube ->
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
        resetShader()

        //terminator.draw()
        //textList.draw()
        //text("Love without hope", 320f, 180f)
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"
    }
}
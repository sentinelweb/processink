package cubes

import cubes.CubesContract.BackgroundShaderType.*
import cubes.CubesContract.ShaderType
import cubes.CubesContract.ShaderType.NEON
import cubes.gui.Controls
import cubes.objects.CubeList
import cubes.objects.TextList
import cubes.ribbons.Ribbons
import cubes.shaders.*
import cubes.util.pushMatrix
import cubes.util.webc
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
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
    private lateinit var nebulaShader: NebulaShader
    private lateinit var flameShader: FlameShader
    private lateinit var refractShader: RefractionPatternShader
    private lateinit var deformShader: DeformShader
    private lateinit var monjoriShader: MonjoriShader
    private var currentShader: ShaderWrapper? = null
    private var currentBackground: ShaderWrapper? = null
    //lateinit var terminator:Terminator
    lateinit var cubesPresenter: CubesPresenter
    private lateinit var ribbons: Ribbons
    private lateinit var buddah: PShape
    private lateinit var lotus: PShape

    lateinit var cubesState: CubesState

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
            .apply { fillColor = Color.YELLOW; visible = false }

        cubesState = CubesState(
            textList = textList,
            cubeList = CubeList(this, textList.texts.size, 50f, 400f).apply { visible = false },
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
        lineShader.setWeight(5f)
        flameShader = FlameShader(this)
        nebulaShader = NebulaShader(this)
        refractShader = RefractionPatternShader(this)
        deformShader = DeformShader(this)
        monjoriShader = MonjoriShader(this)
        hint(PConstants.DISABLE_DEPTH_MASK)
        currentBackground = nebulaShader
        ribbons = Ribbons(this)
        ribbons.setup()
        cubesPresenter.setup()
        buddah = loadShape("${BASE_RESOURCES}/cubes/faith.svg")
        buddah.setStroke(webc("#FAC103"))
        buddah.setFill(webc("#FFF63E"))
        buddah.setStroke(true)
        lotus = loadShape("${BASE_RESOURCES}/cubes/lotus.svg")
        lotus.setStroke(webc("#384FA0"))
        lotus.setFill(webc("#6C8DFF"))
        lotus.setStroke(true)
    }

    // make a algo to send different cubes to catch each other up.
    //  - starting random text nebulaShader shader bg but fill in cubes doesn't work
    override fun draw() {
        cubesPresenter.updateBeforeDraw()
        val color = cubesState.backgroundColor
        background(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
        noStroke()
        currentBackground?.setDefaultShaderParams()
        currentBackground?.engage()

        currentShader?.engage() ?: resetShader()

        cubesState.cubeList.draw()
        resetShader() // doesn't work?

        ribbons.draw()

        cubesState.textList.draw()

        pushMatrix {
            translate(width / 3f, height / 2f)
            draw(buddah)
        }
        pushMatrix {
            translate(width / 3f * 2, height / 2f)
            draw(lotus)
        }
    }

    private fun draw(shape: PShape) {
        val size = 100f
        (shape.getWidth() / shape.getHeight() * size)
            .let { shape(shape, -it / 2f, -size / 2f, it, size) }
    }

    override fun setShaderType(type: ShaderType) {
        when (type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> currentShader = lineShader
            NEON -> currentShader = null // TODO glow shader
        }
    }

    override fun setShaderParam(type: ShaderType, param: String, value: Any) {
        when (type) {
            ShaderType.NONE -> {
            }
            ShaderType.LINES -> lineShader.set(param, value)
            NEON -> flameShader.set(param, value)
        }
    }

    override fun setBackgroundShaderType(type: CubesContract.BackgroundShaderType) {
        when (type) {
            NONE -> currentBackground = null
            NEBULA -> currentBackground = nebulaShader
            COLDFLAME -> currentBackground = flameShader
            REFRACTION_PATTERN -> currentBackground = refractShader
            DEFORM -> currentBackground = deformShader
            MONJORI -> currentBackground = monjoriShader
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
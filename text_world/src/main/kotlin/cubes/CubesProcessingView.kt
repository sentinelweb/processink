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
import cubes.util.wrapper.TimeFormatter
import net.robmunro.processing.util.webc
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import speecher.generator.osc.OscController
import speecher.generator.osc.OscReceiver
import speecher.util.wrapper.LogWrapper
import java.awt.Color

fun main() {
    val cubes = CubesProcessingView()
    val controls = Controls()
    val log = LogWrapper(TimeFormatter())
    val receiver = OscReceiver(log)
    val oscController = OscController(receiver, log)
//    (OscController as OscContract.External).initialise()
//    println("init OSC")
    val presenter = CubesPresenter(controls, cubes, oscController)
    cubes.cubesPresenter = presenter
    cubes.run()
}

/*private*/ class CubesProcessingView : PApplet(), CubesContract.View {
    private lateinit var lineShader: LineShader
    private lateinit var nebulaShader: NebulaShader
    private lateinit var flameShader: FlameShader
    private lateinit var refractShader: RefractionPatternShader
    private lateinit var deformShader: DeformShader
    private lateinit var monjoriShader: MonjoriShader
    private var currentShader: ShaderWrapper? = null
    private var currentBackground: ShaderWrapper? = null

    override fun getApplet(): PApplet = this

    //lateinit var terminator:Terminator
    lateinit var cubesPresenter: CubesPresenter
    private lateinit var ribbons: Ribbons
    private lateinit var yinyang: PShape
    private lateinit var lotus: PShape

    private var lastBackgroundShaderType: CubesContract.BackgroundShaderType? = null

    //lateinit var cubesState: CubesState

    fun getInfo() = PAppletInfo(width, height)

    override fun settings() {
        size(1920, 1080, PConstants.P3D)
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

        val cubesState = CubesState(
            textList = textList,
            cubeList = CubeList(this, textList.texts.size, 50f, 400f).apply { visible = true },
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
        yinyang = loadShape("${BASE_RESOURCES}/cubes/faith.svg")
        yinyang.setStroke(webc("#aaaaaa"))
        yinyang.setFill(webc("#dddddd"))
        yinyang.setStroke(true)
        lotus = loadShape("${BASE_RESOURCES}/cubes/lotus.svg")
        lotus.setStroke(webc("#384FA0"))
        lotus.setFill(webc("#6C8DFF"))
        lotus.setStroke(true)
    }

    // make a algo to send different cubes to catch each other up.
    //  - starting random text nebulaShader shader bg but fill in cubes doesn't work
    override fun draw() {
        cubesPresenter.updateBeforeDraw()
        cubesPresenter.cstate?.apply {
            val color = backgroundColor
            background(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())
            noStroke()

            setBackgroundShaderType(background)
            currentBackground?.setDefaultShaderParams()
            currentBackground?.engage()

            currentShader?.engage() ?: resetShader()

            cubeList.draw()

            resetShader()

            // ribbons.draw()

            textList.draw()

            pushMatrix {
                translate(width / 5f, height / 2f)
                scale(3f)
                draw(yinyang)
            }
            pushMatrix {
                translate(width / 5f * 4, height / 2f)
                scale(3f)
                draw(lotus)
            }
        }
    }

    private fun draw(shape: PShape) {
        val size = 100f
        (shape.getWidth() / shape.getHeight() * size)
            .let { shape(shape, -it / 2f, -size / 2f, it, size) }
    }

    fun setShaderType(type: ShaderType) {
        when (type) {
            ShaderType.NONE -> currentShader = null
            ShaderType.LINES -> currentShader = lineShader
            NEON -> currentShader = null // TODO glow shader
        }
    }

    fun setShaderParam(type: ShaderType, param: String, value: Any) {
        when (type) {
            ShaderType.NONE -> {
            }
            ShaderType.LINES -> lineShader.set(param, value)
            NEON -> flameShader.set(param, value)
        }
    }

    fun setBackgroundShaderType(type: CubesContract.BackgroundShaderType) {
        if (cubesPresenter.cstate?.background != lastBackgroundShaderType) {
            when (type) {
                NONE -> currentBackground = null
                NEBULA -> currentBackground = nebulaShader
                COLDFLAME -> currentBackground = flameShader
                REFRACTION_PATTERN -> currentBackground = refractShader
                DEFORM -> currentBackground = deformShader
                MONJORI -> currentBackground = monjoriShader
            }
            lastBackgroundShaderType = cubesPresenter.cstate?.background
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
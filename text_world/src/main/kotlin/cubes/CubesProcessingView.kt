package cubes

import cubes.CubesContract.BackgroundShaderType.*
import cubes.gui.Controls
import cubes.objects.CubeList
import cubes.objects.TextList
import cubes.osc.*
import cubes.ribbons.Ribbons
import cubes.shaders.*
import cubes.util.wrapper.FilesWrapper
import cubes.util.wrapper.TimeFormatter
import net.robmunro.processing.util.toProcessing
import net.robmunro.processing.util.webc
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Point
import java.io.File

fun main() {
    val cubes = CubesProcessingView()
    val files = FilesWrapper(File(System.getProperty("user.home"), "cubes"))
    val controls = Controls(files)
    val receiver = OscReceiver(LogWrapper(TimeFormatter()), OscMessageMapper(OscTypeTagsParser()))
    val oscController =
        OscController(receiver, OscEventMapper(LogWrapper(TimeFormatter())), LogWrapper(TimeFormatter()), files)
    val presenter = CubesPresenter(controls, cubes, oscController, files)
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
    private lateinit var waterShader: WaterShader
    private lateinit var fujiShader: FujiShader
    private lateinit var fractalPyramidShader: FractalPyramidShader
    private lateinit var octagramsShader: OctagramShader
    private lateinit var proteanCloudsShader: ProteanCloudsShader
    private lateinit var eclipseShader: EclipseShader
    private lateinit var onewarpShader: OneWarpShader
    private lateinit var procWarpShader: ProceduralWarpShader
    private lateinit var cloudsShader: CloudsShader

    private var currentShader: ShaderWrapper? = null
    private var currentBackground: ShaderWrapper? = null

    override fun getApplet(): PApplet = this

    //lateinit var terminator:Terminator
    lateinit var cubesPresenter: CubesPresenter
    private lateinit var ribbons: Ribbons
    private lateinit var yinyang: PShape
    private lateinit var lotus: PShape

    private var lastBackgroundShaderType: CubesContract.BackgroundShaderType? = null

    fun getInfo() = PAppletInfo(width, height)

    override fun settings() {
//        size(320, 180, PConstants.P3D)
//        size(640, 360, PConstants.P3D)
        size(1280, 720, PConstants.P3D)
//        size(1920, 1080, PConstants.P3D)
    }

    override fun setup() {
        //terminator = Terminator(this)
        val cubesState = CubesState(
            textList = TextList(this)
                .apply { fillColor = Color.YELLOW; visible = false },
            cubeList = CubeList(this, 16, 50f, 400f).apply { visible = true },
            rotationSpeed = 0.001f,
            animationTime = 1000f,
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
        waterShader = WaterShader(this)
        waterShader.mouse = Point(604, 595)
        fujiShader = FujiShader(this)
        fractalPyramidShader = FractalPyramidShader(this)
        octagramsShader = OctagramShader(this)
        proteanCloudsShader = ProteanCloudsShader(this)
        eclipseShader = EclipseShader(this)
        onewarpShader = OneWarpShader(this)
        procWarpShader = ProceduralWarpShader(this)
        cloudsShader = CloudsShader(this)

        hint(PConstants.DISABLE_DEPTH_MASK)
        currentBackground = nebulaShader
        ribbons = Ribbons(this)
        ribbons.setup()

        yinyang = loadShape("${BASE_RESOURCES}/cubes/faith.svg")
        yinyang.setStroke(webc("#aaaaaa"))
        yinyang.setFill(webc("#dddddd"))
        yinyang.setStroke(true)
        lotus = loadShape("${BASE_RESOURCES}/cubes/lotus.svg")
        lotus.setStroke(webc("#384FA0"))
        lotus.setFill(webc("#6C8DFF"))
        lotus.setStroke(true)

        cubesPresenter.setup()
    }

    // make a algo to send different cubes to catch each other up.
    //  - starting random text nebulaShader shader bg but fill in cubes doesn't work
    override fun draw() {
        cubesPresenter.updateBeforeDraw()

        cubesPresenter.cstate?.apply {
            noStroke()
            setBackgroundShaderType(background)
            currentBackground?.apply {
                background(Color.BLACK.toProcessing(this@CubesProcessingView))
                fill(Color.BLACK.toProcessing(this@CubesProcessingView))
                color = backgroundColor
                setDefaultShaderParams()
                engage()
            } ?: apply {
                background(
                    backgroundColor.red.toFloat(),
                    backgroundColor.green.toFloat(),
                    backgroundColor.blue.toFloat()
                )
            }

            currentShader?.engage() ?: resetShader()

            cubeList.draw()

            resetShader()

            // ribbons.draw()
//            pushMatrix {
//                translate(width / 5f, height / 2f)
//                scale(3f)
//                draw(yinyang)
//            }
//            pushMatrix {
//                translate(width / 5f * 4, height / 2f)
//                scale(3f)
//                draw(lotus)
//            }
//
            textList.draw()
        }
    }

    private fun draw(shape: PShape) {
        val size = 100f
        (shape.getWidth() / shape.getHeight() * size)
            .let { shape(shape, -it / 2f, -size / 2f, it, size) }
    }

//    fun setShaderType(type: ShaderType) {
//        when (type) {
//            ShaderType.NONE -> currentShader = null
//            ShaderType.LINES -> currentShader = lineShader
//            NEON -> currentShader = null // TODO glow shader
//        }
//    }
//
//    fun setShaderParam(type: ShaderType, param: String, value: Any) {
//        when (type) {
//            ShaderType.NONE -> {
//            }
//            ShaderType.LINES -> lineShader.set(param, value)
//            NEON -> flameShader.set(param, value)
//        }
//    }

    fun setBackgroundShaderType(type: CubesContract.BackgroundShaderType) {
        if (cubesPresenter.cstate?.background != lastBackgroundShaderType) {
            currentBackground = when (type) {
                NONE -> null
                NEBULA -> nebulaShader
                COLDFLAME -> flameShader
                REFRACTION_PATTERN -> refractShader
                DEFORM -> deformShader
                MONJORI -> monjoriShader
                WATER -> waterShader
                FUJI -> fujiShader
                FRACTAL_PYRAMID -> fractalPyramidShader
                OCTAGRAMS -> octagramsShader
                PROTEAN_CLOUDS -> proteanCloudsShader
                ECLIPSE -> eclipseShader
                ONEWARP -> onewarpShader
                PROCWARP -> procWarpShader
                CLOUDS -> cloudsShader
                else -> null
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

}
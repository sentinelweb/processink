package cubes

import cubes.CubesContract.BackgroundShaderType.*
import cubes.gui.Controls
import cubes.models.CubeList
import cubes.models.TextList
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
        val cubesState = CubesState(
            textList = TextList(this)
                .apply { fillColor = Color.YELLOW; visible = false },
            cubeList = CubeList(this, 16, 50f, 400f)
                .apply { visible = true },
            cubesRotationSpeed = 0.001f,
            cubeScale = 10f,
            cubeScaleDist = 0f,
            cubesRotationOffset = 0f,
            cubesFillStartColor = Color.WHITE,
            cubesFillEndColor = Color.GRAY,
            animationTime = 1000f,
            info = getInfo()
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

        // todo move out
        yinyang = loadShape("${BASE_RESOURCES}/svg/faith.svg")
        yinyang.setStroke(webc("#aaaaaa"))
        yinyang.setFill(webc("#dddddd"))
        yinyang.setStroke(true)
        lotus = loadShape("${BASE_RESOURCES}/svg/lotus.svg")
        lotus.setStroke(webc("#384FA0"))
        lotus.setFill(webc("#6C8DFF"))
        lotus.setStroke(true)

        cubesPresenter.setup()
    }

    override fun draw() {
        cubesPresenter.updateBeforeDraw()

        cubesPresenter.state?.apply {
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
            models.forEach { it.draw() }

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

    fun setBackgroundShaderType(type: CubesContract.BackgroundShaderType) {
        if (cubesPresenter.state?.background != lastBackgroundShaderType) {
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
            lastBackgroundShaderType = cubesPresenter.state?.background
        }
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"
    }

}
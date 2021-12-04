package cubes

import cubes.CubesContract.BackgroundShaderType.*
import cubes.gui.Controls
import cubes.osc.*
import cubes.ribbons.Ribbons
import cubes.shaders.*
import cubes.util.wrapper.FilesWrapper
import cubes.util.wrapper.TimeFormatter
import net.robmunro.processing.util.toProcessing
import processing.core.PApplet
import processing.core.PConstants
import speecher.util.wrapper.LogWrapper
import java.awt.Color
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

    override val applet: PApplet
        get() = this

    lateinit var cubesPresenter: CubesPresenter
    private lateinit var ribbons: Ribbons

    private var lastBackgroundShaderType: CubesContract.BackgroundShaderType? = null

    override fun settings() {
//        size(320, 180, PConstants.P3D)
//        size(640, 360, PConstants.P3D)
        size(1280, 720, PConstants.P3D)
//        size(1920, 1080, PConstants.P3D)
    }

    override fun setup() {
        lineShader = LineShader(this)
        lineShader.setWeight(5f)
        flameShader = FlameShader(this)
        nebulaShader = NebulaShader(this)
        refractShader = RefractionPatternShader(this)
        deformShader = DeformShader(this)
        monjoriShader = MonjoriShader(this)
        waterShader = WaterShader(this)
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

            models.forEach { it.draw() }
            // ribbons.draw()

            textList.draw()
        }
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
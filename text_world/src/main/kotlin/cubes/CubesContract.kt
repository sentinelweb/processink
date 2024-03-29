package cubes

import cubes.models.MilleniumFalcon
import cubes.models.Shape
import cubes.models.Terminator
import processing.core.PApplet
import kotlin.reflect.KClass

interface CubesContract {

    enum class BackgroundShaderType {
        NEBULA, COLDFLAME, REFRACTION_PATTERN, DEFORM, MONJORI, WATER, FUJI, FRACTAL_PYRAMID, OCTAGRAMS, PROTEAN_CLOUDS,
        ECLIPSE, CLOUDS, ONEWARP, PROCWARP, HYPERFIELD, STARFIELD_1, BURNING_STAR, NONE
    }

    enum class TextTransition { FADE, FADE_ZOOM, SPIN_X, SPIN_Y, NONE }
    enum class Formation { GRID, LINE, CIRCLE, SQUARE, CENTER }
    enum class RotationAxis { X, Y, Z }

    enum class Model3D(val clazz: KClass<out Shape>) {
        TERMINATOR(Terminator::class),
        MILLENIUM_FALCON(MilleniumFalcon::class)
    }

    enum class ParticleShape { CUBE, CIRCLE, SVG }

    interface View {
        val applet: PApplet
    }

    interface Presenter {
        fun setState(state: CubesState)
        fun setup()
    }

    data class Event constructor(
        val control: Control,
        val data: Any? = null
    )

    enum class Control {
        SHADER_BG,
        BG_COLOR,
        MOTION_ANIMATION_TIME,
        CUBES_ROTATION_SPEED,
        CUBES_ROTATION_OFFEST_RESET,
        CUBES_ROTATION_OFFEST_SPEED,
        CUBES_ROTATION,
        CUBES_ROTATION_RESET,
        CUBES_ROTATION_ALIGN,
        CUBES_VISIBLE,
        CUBES_FORMATION,
        CUBES_SCALE_BASE,
        CUBES_SCALE_OFFSET,
        CUBES_SCALE_APPLY,
        CUBES_COLOR_FILL_START,
        CUBES_COLOR_FILL_END,
        CUBES_FILL,
        CUBES_COLOR_FILL_ALPHA,
        CUBES_COLOR_STROKE,
        CUBES_STROKE,
        CUBES_STROKE_WEIGHT,
        CUBES_LENGTH,
        TEXT_ORDER,
        TEXT_FONT,
        TEXT_MOTION,
        TEXT_COLOR_FILL,
        TEXT_FILL_ALPHA,
        TEXT_VISIBLE,
        TEXT_NEXT,
        TEXT_SET,
        TEXT_GOTO,
        PARTICLE_SYS_CREATE,
        PARTICLE_SHAPE,
        PARTICLE_SHAPE_PATH,
        PARTICLE_STROKE_COLOUR,
        PARTICLE_FILL_COLOUR,
        PARTICLE_NUMBER,
        PARTICLE_SIZE,
        PARTICLE_POSITION,
        PARTICLE_LIFESPAN,
        ADD_MODEL,
        REMOVE_MODEL,
        ADD_IMAGE,
        REMOVE_IMAGE,
        MENU_OPEN_STATE,
        MENU_SAVE_STATE,
        MENU_OPEN_TEXT,
        MENU_SAVE_TEXT,
        MENU_EXIT,
    }
}
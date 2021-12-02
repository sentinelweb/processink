package cubes

import processing.core.PApplet

interface CubesContract {

    enum class ShaderType { NONE, LINES, NEON }

    enum class BackgroundShaderType {
        NEBULA, COLDFLAME, REFRACTION_PATTERN, DEFORM, MONJORI, WATER, FUJI, FRACTAL_PYRAMID, OCTAGRAMS, PROTEAN_CLOUDS,
        ECLIPSE, CLOUDS, ONEWARP, NONE
    }

    enum class TextTransition { FADE, SPIN, FADE_ZOOM }
    enum class Formation { GRID, LINE, CIRCLE, SQUARE, CENTER }
    enum class RotationAxis { X, Y, Z }

    interface View {
        fun getApplet(): PApplet
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
        SHADER_LINE_NONE, SHADER_LINE_LINE, SHADER_LINE_NEON,
        SHADER_BG, BG_COLOR,
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
        TEXT_COLOR_FILL_END,
        TEXT_FILL,
        TEXT_FILL_ALPHA,
        TEXT_COLOR_STROKE,
        TEXT_STROKE_WEIGHT,
        TEXT_STROKE,
        TEXT_VISIBLE,
        MENU_OPEN_STATE,
        MENU_SAVE_STATE,
        MENU_OPEN_TEXT,
        MENU_SAVE_TEXT,
        MENU_EXIT,
    }
}
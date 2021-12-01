package cubes

import processing.core.PApplet

interface CubesContract {

    enum class ShaderType {
        NONE, LINES, NEON
    }

    enum class BackgroundShaderType {
        NEBULA, COLDFLAME, REFRACTION_PATTERN, DEFORM, MONJORI, WATER, FUJI, FRACTAL_PYRAMID, OCTAGRAMS, PROTEAN_COUDS,
        ECLIPSE, CLOUDS, ONEWARP, NONE
    }

    interface View {
        fun getApplet(): PApplet
    }

    interface Presenter {
        fun setState(state: CubesState)
        fun setup()
    }
}
package cubes

import processing.core.PApplet

interface CubesContract {

    enum class ShaderType {
        NONE, LINES, NEON
    }

    enum class BackgroundShaderType {
        NEBULA, COLDFLAME, REFRACTION_PATTERN, DEFORM, MONJORI, NONE
    }

    interface View {
        fun setShaderType(type: ShaderType)
        fun setShaderParam(type: ShaderType, param: String, value: Any)
        fun setBackgroundShaderType(type: BackgroundShaderType)
        fun getApplet(): PApplet
    }

    interface Presenter {
        fun setState(state: CubesState)
        fun setup()
    }
}
package cubes

interface CubesContract {

    enum class ShaderType {
        NONE, LINES, NEON
    }

    enum class BackgroundShaderType {
        NEBULA, COLDFLAME, REFRACTION_PATTERN
    }

    interface View {
        fun setShaderType(type: ShaderType)
        fun setShaderParam(type: ShaderType, param: String, value: Any)
        fun setBackgroundShaderType(type: BackgroundShaderType)
    }

    interface Presenter {
        fun setState(state: CubesState)
        fun setup()
    }
}
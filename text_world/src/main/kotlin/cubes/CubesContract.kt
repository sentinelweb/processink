package cubes

interface CubesContract {
    enum class ShaderType {
        NONE, LINES, NEON
    }


    interface View {
        fun setShaderType(type : ShaderType)
        fun setShaderParam(type: ShaderType, param: String, value: Any)
        fun setCubesMotion(function: (Int, Cube) -> Unit)
    }

    interface Presenter {

    }
}
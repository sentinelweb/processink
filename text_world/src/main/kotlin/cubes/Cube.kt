package cubes

data class Cube constructor(
    val width:Float,
    val height:Float = width,
    val depth:Float = width,
    val texts:List<CharSequence>? = null
){


    companion object {

    }
}
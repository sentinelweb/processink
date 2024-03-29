package cubes.osc

import net.robmunro.processing.util.decodeARGB
import processing.core.PVector
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Font

class OscEventMapper(
    private val log: LogWrapper
) {
    init {
        log.tag(this)
    }

    fun getColor(event: OscContract.OscEvent, default: Color? = Color.BLACK): Color? = kotlin.runCatching {
        when (event.args.size) {
            0 -> {
                default
            }
            1 -> {
                when (event.args[0].type) {
                    "s" -> (event.args[0].value as String)
                        .let { if (it.length == 9) it.decodeARGB() else Color.decode(it) }
                    "f" -> {
                        val grey = event.args[0].value as Float
                        Color(grey, grey, grey)
                    }
                    "i" -> {
                        val grey = event.args[0].value as Int
                        Color(grey, grey, grey)
                    }
                    else -> {
                        log.e("${event.message}: color osc = ${event.args}");
                        default
                    }
                }
            }
            3 -> {
                when (event.args[0].type) {
                    "i" -> {
                        val r = event.args[0].value as Int
                        val g = event.args[1].value as Int
                        val b = event.args[2].value as Int
                        Color(r, g, b)
                    }
                    "f" -> {
                        val r = event.args[0].value as Float
                        val g = event.args[1].value as Float
                        val b = event.args[2].value as Float
                        Color(r, g, b)
                    }
                    else -> {
                        log.e("${event.message}: color osc = ${event.args}");
                        default
                    }
                }
            }
            4 -> {
                when (event.args[0].type) {
                    "i" -> {
                        val r = event.args[0].value as Int
                        val g = event.args[1].value as Int
                        val b = event.args[2].value as Int
                        val a = event.args[3].value as Int
                        Color(r, g, b, a)
                    }
                    "f" -> {
                        val r = event.args[0].value as Float
                        val g = event.args[1].value as Float
                        val b = event.args[2].value as Float
                        val a = event.args[3].value as Float
                        Color(r, g, b, a)
                    }
                    else -> {
                        log.e("${event.message}: color osc = ${event.args}")
                        default
                    }
                }
            }
            else -> {
                log.e("${event.message}: color osc = ${event.args}")
                default
            }
        }
    }.getOrElse {
        log.e("${event.message}: color osc = ${event.args}")
        default
    }

    fun getString(event: OscContract.OscEvent, arg: Int): String =
        if (event.args[arg].type == "s") {
            (event.args[arg].value as String)
        } else if (event.args[arg].type == "i") {
            (event.args[arg].value as Int).toString()
        } else if (event.args[arg].type == "f") {
            (event.args[arg].value as Float).toString()
        } else {
            log.e("${event.message}: arg:$arg not a string = ${event.args[arg]}");
            ""
        }

    fun getStringOrNull(event: OscContract.OscEvent, arg: Int): String? =
        if (event.args[arg].type == "s") {
            (event.args[arg].value as String)
        } else if (event.args[arg].type == "i") {
            (event.args[arg].value as Int).toString()
        } else if (event.args[arg].type == "f") {
            (event.args[arg].value as Float).toString()
        } else {
            null
        }

    fun <E : Enum<E>> getEnum(event: OscContract.OscEvent, arg: Int, def: E): E = if (event.args[arg].type == "s") {
        def::class.java.enumConstants.find { it.name == event.args[arg].value as String } ?: def
    } else {
        log.e("${event.message}: arg:$arg not a enum value of ${def::class.java.simpleName}= ${event.args[arg]}");
        def
    }

    fun getBoolean(event: OscContract.OscEvent, arg: Int): Boolean = when (event.args[arg].type) {
        "i" -> (event.args[arg].value as Int) == 1
        "s" -> event.args[arg].value == "true"
        "f" -> (event.args[arg].value as Float) == 1f
        else -> {
            log.e("${event.message}: arg:$arg not a boolean = ${event.args[arg]}");
            false
        }
    }

    fun getInt(event: OscContract.OscEvent, arg: Int): Int = when (event.args[arg].type) {
        "i" -> (event.args[arg].value as Int)
        "f" -> (event.args[arg].value as Float).toInt()
        "s" -> (event.args[arg].value as String).toInt()
        else -> {
            log.e("${event.message}: arg:$arg not a int = ${event.args[arg]}");
            0
        }
    }

    fun getInt0To255(event: OscContract.OscEvent, arg: Int): Int = when (event.args[arg].type) {
        "i" -> (event.args[arg].value as Int)
        "f" -> (event.args[arg].value as Float)
            .let { if (it > 0 && it <= 1) (it * 255).toInt() else it.toInt() }
        "s" -> (event.args[arg].value as String).toInt()
        else -> {
            log.e("${event.message}: arg:$arg not a int = ${event.args[arg]}");
            0
        }
    }

    fun getFloat(event: OscContract.OscEvent, arg: Int): Float = when (event.args[arg].type) {
        "i" -> (event.args[arg].value as Int).toFloat()
        "f" -> (event.args[arg].value as Float)
        "s" -> (event.args[arg].value as String).toFloat()
        else -> {
            log.e("${event.message}: arg:$arg not a float = ${event.args[arg]}");
            0f
        }
    }

    fun getFloat0To1(event: OscContract.OscEvent, arg: Int): Float = when (event.args[arg].type) {
        "i" -> (event.args[arg].value as Int / 255f).toFloat()
        "f" -> (event.args[arg].value as Float)
        "s" -> (event.args[arg].value as String).toFloat()
        else -> {
            log.e("${event.message}: arg:$arg not a float = ${event.args[arg]}");
            0f
        }
    }

    fun getFont(e: OscContract.OscEvent): Font? =
        (e.args.get(0).value as? String)?.let { name ->
            if (e.args.size > 1) {
                when (e.args.get(1).type) {
                    "s" -> Font(name, Font.PLAIN, getInt(e, 1))
                    "f" -> Font(name, Font.PLAIN, getInt(e, 1))
                    "i" -> Font(name, Font.PLAIN, getInt(e, 1))
                    else -> Font(name, Font.PLAIN, 60)
                }
            } else {
                Font(name, Font.PLAIN, 60)
            }
        }

    fun getPVector(e: OscContract.OscEvent): PVector =
        PVector(
            getFloat(e, 0),
            getFloat(e, 1),
            getFloat(e, 2)
        )
}
package speecher.util.wrapper

import cubes.util.wrapper.TimeFormatter
import java.time.LocalTime
import kotlin.reflect.KClass

class LogWrapper constructor(private val timeFormatter: TimeFormatter, var tag: String? = null) {

    constructor(timeFormatter: TimeFormatter, clazz: KClass<Any>) : this(
        timeFormatter,
        clazz.simpleName ?: clazz.qualifiedName
    )

    private var startTime: LocalTime? = null

    var time: Boolean = false

    fun tag(obj: Any) {
        tag = obj::class.simpleName ?: obj::class.qualifiedName
    }

    fun startTime() {
        startTime = LocalTime.now()
    }

    fun stoptTime() {
        startTime = null
    }

    private fun timeLogString() = when {
        startTime != null -> startTime?.let { "[${timeFormatter.formatFrom(it)}] " }
        time -> "[${timeFormatter.formatNow()}] "
        else -> ""
    }

    fun d(msg: String) = println("${timeLogString()}$tag: $msg")

    fun e(msg: String) = System.err.println("$tag: $msg")

    fun e(msg: String, err: Throwable) {
        System.err.println("$tag: $msg")
        err.printStackTrace()
    }
}
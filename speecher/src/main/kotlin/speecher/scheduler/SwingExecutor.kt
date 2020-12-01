package speecher.scheduler

import speecher.util.format.TimeFormatter
import speecher.util.wrapper.LogWrapper
import java.awt.EventQueue
import java.util.concurrent.Executor
import javax.swing.SwingUtilities

class SwingExecutor(private val log: LogWrapper) : Executor {

    init {
        log.tag(this)
    }

    override fun execute(command: Runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            log.d("execute now: ${currentThreadInfo()}")
            command.run()
        } else {
            log.d("execute later: ${currentThreadInfo()}")
            //SwingUtilities.invokeLater(command)
            EventQueue.invokeAndWait(command)
        }
    }

}

fun checkSwingThread() {
    if (!SwingUtilities.isEventDispatchThread()) {
        //throw IncorrectThreadException("Not called from swing thread")
        IncorrectThreadException("Not called from swing thread - current:  ${currentThreadInfo()}").printStackTrace()
        System.exit(0)
    }
}

class IncorrectThreadException(msg: String) : Exception(msg)

fun setUncaughtExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
        LogWrapper(TimeFormatter(), tag = "ExceptionHandler").apply {
            e("Uncaught Exception on " + threadInfo(thread), ex)
            System.exit(0)
        }
    }
}

fun threadInfo(thread: Thread) =
    "Thread: id = ${thread.id} name = ${thread.name} alive=${thread.isAlive} hashcode=${thread.hashCode()}"

fun currentThreadInfo() = threadInfo(Thread.currentThread())
package speecher.scheduler


import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

class ProcessingExecutor : Executor {
    val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()

    override fun execute(command: Runnable) {
        workQueue.add(command)
    }
}
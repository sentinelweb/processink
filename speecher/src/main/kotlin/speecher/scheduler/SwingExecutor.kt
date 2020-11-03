package speecher.scheduler

import java.awt.EventQueue
import java.util.concurrent.Executor
import javax.swing.SwingUtilities

class SwingExecutor : Executor {

    override fun execute(command: Runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            command.run();
        } else {
            EventQueue.invokeLater(command);
        }
    }
}
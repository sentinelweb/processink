package speecher.scheduler

import io.reactivex.schedulers.Schedulers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.Executors

object SchedulerModule {

    val PROCESSING = "Processing"
    val SWING = "Swing"
    val PLAYER = "Player"

    val module = module {
        single { ProcessingExecutor() }
        single { SwingExecutor() }
        single(named(PROCESSING)) { Schedulers.from(get<ProcessingExecutor>()) }
        single(named(SWING)) { Schedulers.from(get<SwingExecutor>()) }
        single(named(PLAYER)) { Schedulers.from(Executors.newSingleThreadExecutor()) }
    }

}
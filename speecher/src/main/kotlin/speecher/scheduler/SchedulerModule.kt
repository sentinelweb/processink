package speecher.scheduler

import io.reactivex.schedulers.Schedulers
import org.koin.core.qualifier.named
import org.koin.dsl.module

object SchedulerModule {
    val PROCESSING = "Processing"
    val SWING = "Swing"
    val module = module {
        single { ProcessingExecutor() }
        single { SwingExecutor() }
        single(named(PROCESSING)) { Schedulers.from(get<ProcessingExecutor>()) }
        single(named(SWING)) { Schedulers.from(get<SwingExecutor>()) }
    }


}
package speecher.scheduler

import hu.akarnokd.rxjava2.swing.SwingSchedulers
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
        single { SwingExecutor(get()) }
        single(named(PROCESSING)) { Schedulers.from(get<ProcessingExecutor>()) }
        //single(named(SWING)) { Schedulers.from(get<SwingExecutor>()) }
        single(named(SWING)) { SwingSchedulers.edt() }
//        single(named(PLAYER)) { Schedulers.from(Executors.newSingleThreadExecutor()) }
        single(named(PLAYER)) { Schedulers.from(Executors.newFixedThreadPool(10)) }
    }

}
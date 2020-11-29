package speecher.generator.osc

import org.koin.core.qualifier.named
import org.koin.dsl.module

interface OscContract {

    interface Receiver {
        fun start(port: Int)
        fun shutdown()
    }

    interface Controler {
        fun initialise()
        fun shutdown()
    }

    companion object {
        @JvmStatic
        val scopeModule = module {
            single<Controler> { OscController() }
            scope(named<OscController>()) {
                scoped<Receiver> { OscReceiver(get()) }
            }
        }
    }
}
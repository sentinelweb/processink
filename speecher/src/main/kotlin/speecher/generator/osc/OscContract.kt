package speecher.generator.osc

import org.koin.core.qualifier.named
import org.koin.dsl.module

interface OscContract {

    interface Receiver {
        fun start(port: Int)
        fun shutdown()
        fun setController(c: Controller)
        fun isRunning(): Boolean
    }

    interface Controller {
        fun processEvent(e: Event)
    }

    interface External {
        var listener: Listener
        fun initialise()
        fun shutdown()
        fun isRunning(): Boolean
    }

    interface Listener {
        fun onReceiverStarted()
        fun onReceiverStopped()
        fun onPlaySentence()
        fun onPlayNextWord()
        fun onRewindSentence()
        fun onLooping(loop: Boolean)
        fun onVolume(vol: Float)
        fun onLoadSentence(index: Int)
        fun onPause()
        fun onPlayOneWord(playOneWord: Boolean)
    }

    data class Event(
        val message: String,
        val args: List<Any>,
        val typeTags: CharSequence
    )

    companion object {
        @JvmStatic
        val scopeModule = module {
            single<External> { OscController() }
            scope(named<OscController>()) {
                scoped<Receiver> { OscReceiver(get()) }
            }
        }
    }
}
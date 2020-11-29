package speecher.generator.osc

import org.koin.core.qualifier.named
import org.koin.dsl.module

interface OscContract {

    interface Receiver {
        fun start(port: Int)
        fun shutdown()
        fun setController(c: Controller)
    }

    interface Controller {
        var listener: Listener
        fun initialise()
        fun processEvent(e: Event)
        fun shutdown()
    }

    interface Listener {
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
            single<Controller> { OscController() }
            scope(named<OscController>()) {
                scoped<Receiver> { OscReceiver(get()) }
            }
        }
    }
}
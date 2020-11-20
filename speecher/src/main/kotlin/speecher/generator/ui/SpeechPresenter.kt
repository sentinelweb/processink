package speecher.generator.ui

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.di.Modules

fun main() {
    startKoin { modules(Modules.allModules) }
    SpeechPresenter().showWindow()
}

class SpeechPresenter : SpeechContract.Presenter {

    private val scope = this.getOrCreateScope()
    private val view: SpeechContract.View = scope.get()
    private val state: SpeechState = scope.get()

    override fun showWindow() {
        view.showWindow()
    }

    override fun moveCursorForward() {
        println("moveCursorForward")
    }

    override fun moveCursorBack() {
        println("moveCursorBack")
    }

    override fun play() {
        println("play")
    }

    override fun pause() {
        println("pause")
    }

    companion object {

        @JvmStatic
        val scope = module {
            scope(named<SpeechPresenter>()) {
                scoped<SpeechContract.Presenter> { getSource() }
                scoped<SpeechContract.View> { SpeechView(get()) }
                scoped { SpeechState() }
            }
        }
    }
}
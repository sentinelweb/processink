package speecher.generator.ui

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import javax.swing.SwingUtilities

fun main() {
    startKoin { modules(Modules.allModules) }
    SpeechPresenter().apply {
        listener = object : SpeechContract.Listener {
            override fun sentenceChanged(sentence: Sentence) {
                println("listener:sentence")
            }

            override fun play() {
                println("listener:play")
            }

            override fun pause() {
                println("listener:pause")
            }

        }
        showWindow()
        SwingUtilities.invokeLater {
            setSubs((0..300).mapIndexed { i, e ->
                Subtitles.Subtitle(e.toFloat(), e.toFloat() + 1, listOf("subtitle $i"))
            })
        }
    }
}

class SpeechPresenter :
    SpeechContract.Presenter,
    SpeechContract.External,
    SubtitleChipView.Listener {

    private val scope = this.getOrCreateScope()
    private val view: SpeechContract.View = scope.get()
    private val state: SpeechState = scope.get()

    // region presenter
    override fun moveCursor(pos: SpeechContract.CursorPosition) {
        println("moveCursor $pos")
    }

    override fun sortOrder(order: SpeechContract.SortOrder) {
        println("sort $order")
    }

    override fun play() {
        println("play")
    }

    override fun pause() {
        println("pause")
    }

    override fun searchText(text: String) {
        println("search $text")
    }

    override fun openSubs() {
        println("openSubs")
    }
    // endregion

    // region External
    override lateinit var listener: SpeechContract.Listener

    override fun setSubs(subs: List<Subtitles.Subtitle>) {
        state.subs = subs
        view.updateSubList(subs)
    }

    override fun showWindow() {
        view.showWindow()
    }

    // endregion

    // region SubtitleChipView.Listener
    override fun onItemClicked(sub: Subtitles.Subtitle) {
        println("subchip.onItemClicked $sub")
    }

    override fun onPreviewClicked(sub: Subtitles.Subtitle) {
        println("subchip.onPreviewClicked $sub")
    }
    // endregion


    companion object {

        @JvmStatic
        val scope = module {
            scope(named<SpeechPresenter>()) {
                scoped<SpeechContract.Presenter> { getSource() }
                scoped<SubtitleChipView.Listener> { getSource() }
                scoped<SpeechContract.View> { SpeechView(get(), get(), get()) }
                scoped { SpeechState() }
            }
        }
    }

}
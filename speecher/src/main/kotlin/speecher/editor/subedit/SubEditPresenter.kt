package speecher.editor.subedit

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles

class SubEditPresenter : SubEditContract.Presenter, SubEditContract.External {
    private val scope = this.getOrCreateScope()
    private val view: SubEditContract.View = scope.get()
    private val state: SubEditState = scope.get()

    // region SubEditContract.External
    override lateinit var listener: SubEditContract.Listener

    override fun showWindow() {
        view.showWindow()
    }

    override fun setReadSub(sub: Subtitles.Subtitle) {
        state.readSub = sub
        state.readWordList.clear()
        state.readWordList.addAll(sub.text.map { it.split(" ") }.flatten())
        view.setWordList(state.readWordList)
        view.setLimits(sub.fromSec, sub.toSec)
    }

    override fun setWriteSubs(subs: List<Subtitles.Subtitle>) {

    }
    // endregion

    // region SubEditContract.Presenter
    override fun wordSelected(index: Int) {
        println("wordSelected($index)")
    }

    override fun sliderChanged(index: Int, time: Float) {
        println("sliderChanged($index, $time)")
    }

    override fun onSave(moveToNext: Boolean) {
        println("onSave(moveToNext: $moveToNext)")
    }
    // endregion

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<SubEditPresenter>()) {
                scoped<SubEditContract.View> { SubEditView(get(), get()) }
                scoped { SubEditState() }
            }
        }
    }
}
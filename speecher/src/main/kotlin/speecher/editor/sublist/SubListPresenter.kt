package speecher.editor.sublist

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles

class SubListPresenter : SubListContract.Presenter, SubListContract.External {

    private val scope = this.getOrCreateScope()
    private val view: SubListContract.View = scope.get()
    private val state: SubListState = scope.get()

    override lateinit var listener: SubListContract.Listener

    // region SubListContract.Presenter
    override fun onItemClicked(index: Int) {
        println("onItemClicked -> $index")
        state.subtitles?.timedTexts?.get(index)?.let { sub ->
            listener.onItemSelected(sub, index)
        }
    }
    // endregion

    // region SubListContract.External
    override fun setList(subs: Subtitles) {
        state.subtitles = subs
        view.buildList(subs)
    }

    override fun showWindow() {
        view.showWindow()
    }

    override fun setTitle(title: String) {
        view.setTitle(title)
    }
    // endregion

    companion object {
        @JvmStatic
        val viewModule = module {
            scope(named<SubListPresenter>()) {
                scoped<SubListContract.View> { SubListView(get(), get()) }
                scoped { SubListState() }
            }
        }
    }
}
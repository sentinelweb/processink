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

    override fun searchText(text: String) {
        state.searchText = text
        buildList()
    }
    // endregion

    // region SubListContract.External
    override fun setList(subs: Subtitles) {
        state.subtitles = subs
        buildList()
    }

    private fun buildList() {
        state.subtitlesDisplay = state.subtitles?.timedTexts
            ?.mapIndexed { i, sub -> i to sub }
            ?.filter { (_, v) ->
                state.searchText
                    ?.let { st -> v.text.any { it.contains(st) } }
                    ?: true
            }
            ?.toMap()
            ?: mapOf()
        state.subtitlesDisplay.let { view.buildList(it) }
    }

    override fun showWindow(x: Int, y: Int) {
        view.showWindow(x, y)
    }

    override fun setSelected(index: Int?) {
        state.selectedIndex?.let { view.clearSelected(it) }
        index?.let { view.setSelected(it) }
        state.selectedIndex = index
    }

    override fun setTitle(title: String) {
        view.setTitle(title)
    }
    // endregion

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<SubListPresenter>()) {
                scoped<SubListContract.View> { SubListView(get(), get()) }
                scoped { SubListState() }
            }
        }
    }
}
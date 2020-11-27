package speecher.generator.ui.sentence_list

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Sentence

class SentenceListPresenter : SentenceListContract.Presenter, SentenceListContract.External {

    private val scope = this.getOrCreateScope()
    private val view: SentenceListContract.View = scope.get()
    private val state: SentenceListState = scope.get()

    override lateinit var listener: SentenceListContract.Listener

    // region SentenceListContract.Presenter
    override fun onItemClicked(key: String) {
        println("onItemClicked -> $key")
        state.sentences?.get(key)?.let { sent ->
            listener.onItemSelected(key, sent)
        }
    }

    override fun onDelete(key: String) {
        view.showDeleteConfirm("Delete $key?") {
            state.sentences.remove(key)
            updateSentenceList()
        }
    }
    // endregion

    // region SentenceListContract.External
    override fun setList(sentences: Map<String, Sentence>) {
        state.sentences.apply {
            clear()
            putAll(sentences)
            updateSentenceList()
        }
    }

    private fun updateSentenceList() {
        view.buildList(state.sentences.mapValues { (_, v) -> v.words.map { it.sub.text[0] }.joinToString(" ") })
    }

    override fun showWindow() {
        view.showWindow()
        updateSentenceList()
    }

    override fun setSelected(key: String?) {
        state.selectedKey?.let { view.clearSelected(it) }
        key?.let { view.setSelected(it) }
        state.selectedKey = key
    }

    override fun putSentence(id: String, wordSentence: Sentence) {
        state.sentences[id] = wordSentence
        updateSentenceList()
    }

    override fun getList(): Map<String, Sentence> = state.sentences
    // endregion

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<SentenceListPresenter>()) {
                scoped<SentenceListContract.View> { SentenceListView(get(), get()) }
                scoped { SentenceListState() }
            }
        }
    }
}
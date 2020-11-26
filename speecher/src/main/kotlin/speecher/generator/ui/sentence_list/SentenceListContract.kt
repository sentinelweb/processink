package speecher.generator.ui.sentence_list

import speecher.domain.Sentence

interface SentenceListContract {

    interface Presenter {
        fun onItemClicked(key: String)
    }

    interface External {
        var listener: Listener
        fun setList(sentences: Map<String, Sentence>)
        fun showWindow()
        fun setSelected(key: String?)
    }

    interface Listener {
        fun onItemSelected(key: String, sentence: Sentence)
    }

    interface View {
        fun buildList(sentences: Map<String, String>) // should be a model
        fun showWindow()
        fun setSelected(key: String)
        fun clearSelected(key: String)
    }
}
package speecher.editor.sublist

import speecher.domain.Subtitles

interface SubListContract {

    interface Presenter {
        fun onItemClicked(index: Int)
    }

    interface External {
        var listener: Listener
        fun setList(subs: Subtitles)
        fun showWindow(x: Int = 0, y: Int = 0)
        fun setTitle(title: String)
    }

    interface Listener {
        fun onItemSelected(sub: Subtitles.Subtitle, index: Int)
    }

    interface View {
        fun setTitle(title: String)
        fun buildList(subs: Subtitles) // should be a model
        fun showWindow(x: Int, y: Int)
    }
}
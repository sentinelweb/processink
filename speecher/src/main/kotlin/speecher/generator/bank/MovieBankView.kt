package speecher.generator.bank

import processing.video.Movie
import speecher.generator.movie.MovieContract

class MovieBankView : MovieBankContract.View {

    override var active: Int = -1
    private val movieViews: MutableList<MovieContract.View> = mutableListOf()

    override fun render() {
        if (active > -1 && active < movieViews.size) movieViews[active].render()
    }

    // region movie
    override fun movieEvent(m: Movie) {
        m.read()
        movieViews.find { it.hasMovie(m) }?.movieEvent(m)
    }
    // endregion

    override fun addMovieView(view: MovieContract.View) {
        movieViews.add(view)
    }

    override fun cleanup() {
        movieViews.forEach { it.cleanup() }
        movieViews.clear()
    }
    // endregion
}
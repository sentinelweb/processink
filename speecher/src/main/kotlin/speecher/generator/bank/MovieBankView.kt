package speecher.generator.bank

import processing.video.Movie
import speecher.generator.movie.MovieContract

class MovieBankView : MovieBankContract.View, MovieContract.Sketch {

    override var active: Int = -1
    private val movieViews: MutableList<MovieContract.View> = mutableListOf()

    override fun render() {
        if (active > -1) movieViews[active].render()
    }

    // region movie
    override fun movieEvent(m: Movie) {
        m.read()
        movieViews.find { it.hasMovie(m) }?.movieEvent(m)
    }
    // endregion

    // region MovieContract.Sketch
    override fun addView(v: MovieContract.View) {
        movieViews.add(v)
    }

    override fun cleanup() {
        movieViews.forEach { it.cleanup() }
        movieViews.clear()
    }
    // endregion
}
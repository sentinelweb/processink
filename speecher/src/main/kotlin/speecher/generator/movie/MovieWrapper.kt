package speecher.generator.movie

import processing.core.PApplet
import processing.video.Movie

class MovieWrapper(val p: PApplet, fileName: String) : Movie(p, fileName) {

    fun isSeeking() = seeking

    fun playerState() = playbin.state

}
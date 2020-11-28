package speecher.generator.movie

import org.koin.core.qualifier.named
import org.koin.dsl.module
import speecher.util.wrapper.LogWrapper

class MovieCreator {

    fun create(
        index: Int,
        log: LogWrapper,
        listener: MovieContract.Listener
    ): MovieContract.External =
        MoviePresenter(index, log)
            .also { bank -> bank.listener = listener }

    companion object {
        @JvmStatic
        val scopeModule = module {
            scope(named<MoviePresenter>()) {
                scoped<MovieContract.Presenter> { getSource() }
                scoped<MovieContract.View> {
                    MovieView(
                        presenter = get(),
                        state = get(),
                        p = get(),
                        log = get()
                    )
                }
                scoped { MovieState() }
            }
        }
    }
}
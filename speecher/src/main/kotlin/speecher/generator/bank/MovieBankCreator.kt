package speecher.generator.bank

class MovieBankCreator {
    fun create(listener: MovieBankContract.Listener): Pair<MovieBankContract.External, MovieBankContract.View> {
        val bankView = MovieBankView()
        val bankPresenter = MovieBankPresenter(MovieBankContract.State(), bankView, 10)
            .also { bank -> bank.listener = listener }

        return bankPresenter to bankView
    }
}
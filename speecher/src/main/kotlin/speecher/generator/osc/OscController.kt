package speecher.generator.osc

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.ext.getOrCreateScope
import speecher.util.wrapper.LogWrapper

class OscController : OscContract.Controler {
    private val scope = this.getOrCreateScope()
    private val receiver: OscContract.Receiver = scope.get()
    private val log: LogWrapper = scope.get()
    private val disposables = CompositeDisposable()

    init {
        log.tag(this)
    }

    override fun initialise() {
        Completable.fromCallable {
            receiver.start(port)
        }.subscribeOn(Schedulers.io())
            //.subscribeOn(Schedulers.io())
            .subscribe({
                //log.d("Osc receiver started ..")
            }, { t -> log.e("OSC receiver start exception", t) })
            .also { disposables.add(it) }
    }

    override fun shutdown() {
        receiver.shutdown()
        disposables.clear()
    }

    companion object {
        private const val port = 1239
    }
}
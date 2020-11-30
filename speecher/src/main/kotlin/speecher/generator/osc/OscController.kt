package speecher.generator.osc

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.ext.getOrCreateScope
import speecher.util.wrapper.LogWrapper
import java.lang.Float.max
import java.lang.Float.min

class OscController : OscContract.Controller, OscContract.External {
    private val scope = this.getOrCreateScope()
    private val receiver: OscContract.Receiver = scope.get()
    private val log: LogWrapper = scope.get()
    private val disposables = CompositeDisposable()

    init {
        log.tag(this)
        receiver.setController(this)
    }

    override lateinit var listener: OscContract.Listener

    override fun initialise() {
        Completable.fromCallable {
            receiver.start(port)
        }.subscribeOn(Schedulers.io())
            //.subscribeOn(Schedulers.io())
            .subscribe({
                listener.onReceiverStarted()
                //log.d("Osc receiver started ..")
            }, { t -> log.e("OSC receiver start exception", t) })
            .also { disposables.add(it) }
    }

    override fun processEvent(event: OscContract.Event) {
        try {
            when (event.message.substring("/oscControl".length)) {
                "/loadSentence" -> listener.onLoadSentence((event.args[0] as Float).toInt())
                "/playNextWord" -> listener.onPlayNextWord()
                "/rewindSentence" -> listener.onRewindSentence()
                "/looping" -> listener.onLooping(event.args[0] as Float == 1f)
                "/volume" -> listener.onVolume(min(max(event.args[0] as Float, 0f), 1f))
                "/playSentence" -> listener.onPlaySentence()
                "/pause" -> listener.onPause()
                "/playOneWord" -> listener.onPlayOneWord(event.args[0] as Float == 1f)
                else -> {
                    log.d("no handler for message ${event.message}")
                }
            }
        } catch (ex: Throwable) {
            log.e("Cannot process message: ${event.message}", ex)
        }
    }

    override fun shutdown() {
        receiver.shutdown()
        disposables.clear()
        if (this::listener.isInitialized) {
            listener.onReceiverStopped()
        }
    }

    override fun isRunning(): Boolean = receiver.isRunning()

    companion object {
        private const val port = 1239
    }
}
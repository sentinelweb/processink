package speecher.generator.osc

import speecher.util.wrapper.LogWrapper

class OscController(
    private val receiver: OscContract.Receiver,
    private val log: LogWrapper
) : OscContract.Controller, OscContract.External {
//    private val disposables = CompositeDisposable()

    init {
        log.tag(this)
        receiver.setController(this)
    }

    override lateinit var listener: OscContract.Listener

    override fun initialise() {
        log.d("Osc receiver initialising ..")
        receiver.start(port)
        log.d("Osc receiver started ..")
//        Completable.fromCallable {
//
//        }.subscribeOn(Schedulers.io())
//            .subscribe({
//                //listener.onReceiverStarted()
//                log.d("Osc receiver started ..")
//            }, { t -> log.e("OSC receiver start exception", t) })
//            .also { disposables.add(it) }
    }

    override fun processEvent(e: OscContract.Event) {
        try {
            when (e.message) {
//                "/loadSentence" -> listener.onLoadSentence((event.args[0] as Float).toInt())
//                "/playNextWord" -> listener.onPlayNextWord()
//                "/rewindSentence" -> listener.onRewindSentence()
//                "/looping" -> listener.onLooping(event.args[0] as Float == 1f)
//                "/volume" -> listener.onVolume(min(max(event.args[0] as Float, 0f), 1f))
//                "/playSentence" -> listener.onPlaySentence()
//                "/pause" -> listener.onPause()
//                "/playOneWord" -> listener.onPlayOneWord(event.args[0] as Float == 1f)
                else -> {
                    log.d("no handler for message ${e.message}")
                }
            }
        } catch (ex: Throwable) {
            log.e("Cannot process message: ${e.message}", ex)
        }
    }

    override fun shutdown() {
        receiver.shutdown()
        //disposables.clear()
        if (this::listener.isInitialized) {
            listener.onReceiverStopped()
        }
    }

    override fun isRunning(): Boolean = receiver.isRunning()

    companion object {
        private const val port = 1239
    }
}
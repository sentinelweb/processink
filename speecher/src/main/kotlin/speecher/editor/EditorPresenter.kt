package speecher.editor

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import speecher.domain.Subtitles
import speecher.editor.subedit.SubEditContract
import speecher.editor.sublist.SubListContract
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.interactor.srt.SrtInteractor
import speecher.util.subs.SubFinder
import speecher.util.subs.SubTracker
import java.io.File

class EditorPresenter constructor(
    private val view: EditorContract.View,
    private val state: EditorState,
    private val transport: TransportContract.External,
    private val srtInteractor: SrtInteractor,
    private val readSubs: SubListContract.External,
    private val writeSubs: SubListContract.External,
    private val subEdit: SubEditContract.External,
    private val pScheduler: Scheduler,
    private val swingScheduler: Scheduler,
    private val subFinder: SubFinder,
    private val readSubTracker: SubTracker,
    private val writeSubTracker: SubTracker
) : EditorContract.Presenter, TransportContract.StateListener {

    private val disposables: CompositeDisposable = CompositeDisposable()

    // region SubListContract.Listener - Read & Write
    private val readSubListListener = object : SubListContract.Listener {
        override fun onItemSelected(sub: Subtitles.Subtitle, index: Int) {
            val positionSec = sub.fromSec
            jumpTo(positionSec)
            setLooping(sub.fromSec, sub.toSec)
            selectSubtitle(EditorState.SelectedSubtitle(index, true))
            subEdit.setReadSub(sub)
            state.srtWrite?.let { subEdit.setWriteSubs(subFinder.getRangeExclusive(sub, it)) }
        }
    }

    private val writeSubListListener = object : SubListContract.Listener {
        override fun onItemSelected(sub: Subtitles.Subtitle, index: Int) {
            val positionSec = sub.fromSec
            jumpTo(positionSec)
            setLooping(sub.fromSec, sub.toSec)
            selectSubtitle(EditorState.SelectedSubtitle(index, false))
        }
    }

    private fun selectSubtitle(selectedSubtitle: EditorState.SelectedSubtitle?) {
        state.selectedSubtitle?.let {
            (if (it.isRead) readSubs else writeSubs).setSelected(null)
        }
        state.selectedSubtitle = selectedSubtitle?.apply {
            (if (isRead) readSubs else writeSubs).setSelected(index)
        }
    }
    // endregion

    // region SubEditContract.Listener - Read & Write
    private val editSubListener = object : SubEditContract.Listener {

        override fun onLoopChanged(fromSec: Float, toSec: Float) {
            if (toSec - fromSec < 0.05) {
                transport.setStatus("TOO SHORT : not setting loop reigon $fromSec -> $toSec ")
                return
            }
            setLooping(fromSec, toSec)
            if (fromSec != state.loopStartSec) {
                jumpTo(fromSec)
            }
        }

        override fun saveWriteSubs(subs: List<Subtitles.Subtitle>) {
            if (subs.size > 0) {
                val existing = state.srtWrite?.timedTexts.orEmpty().toMutableList()
                subs.forEach { sub ->
                    subFinder.findOverlapping(sub, existing)
                        ?.let {
                            existing.set(it, sub)
                        } ?: existing.add(sub)
                }
                existing.sortBy { it.fromSec }
                (state.srtWrite?.copy(timedTexts = existing) ?: Subtitles(timedTexts = existing))
                    .let {
                        state.srtWrite = it
                        writeSubs.setList(it)
                    }
                state.srtWriteFile?.let { saveWriteSrt(it) } ?: saveAs()
            }
        }

        override fun markDirty() {
            state.isDirty = true
        }
    }
    // endregion

    init {
        transport.listener = this
        transport.showWindow()
        subEdit.listener = editSubListener
        subEdit.showWindow()
        readSubs.listener = readSubListListener
        writeSubs.listener = writeSubListListener
        readSubTracker.provider = object : SubTracker.SubProvider {
            override fun getSubs(): Subtitles? {
                return state.srtRead
            }
        }
        writeSubTracker.provider = object : SubTracker.SubProvider {
            override fun getSubs(): Subtitles? {
                return state.srtWrite
            }
        }
        disposables.add(
            transport.events()
                .subscribe({
                    println("editor event: ${it.uiEventType} -> ${it.data}")
                    processEvent(it)
                }, {
                    println("error: ${it.localizedMessage}")
                    it.printStackTrace()
                })
        )
    }


    // region Presenter
    override val currentReadSubtitle: String?
        get() = readSubTracker.currentSubtitle

    override val currentWriteSubtitle: String?
        get() = writeSubTracker.currentSubtitle

    override fun duration(dur: Float) {
        state.movieDurationSec = dur
        transport.setDuration(dur)
    }

    override fun position(pos: Float) {
        state.moviePositionSec = pos
        transport.setPosition(pos)
        readSubTracker.checkSubtitle(pos)
        writeSubTracker.checkSubtitle(pos)
        checkLoopingPos(pos)
    }

    override fun setPlayState(mode: TransportContract.UiDataType) {
        transport.setPlayState(mode)
    }

    override fun movieInitialised() {
        transport.updateState()
    }

    override fun initialise() {
        readSubs.showWindow()
        readSubs.setTitle("Read Subtitles")
        writeSubs.showWindow(1330, 0)
        writeSubs.setTitle("Write Subtitles")
        Single.concat(
            openMovieSingle(File(EditorView.DEF_MOVIE_PATH)),
            srtReadOpenSingle(File(EditorView.DEF_SRT_PATH)),
            srtWriteOpenSingle(File(EditorView.DEF_WRITE_SRT_PATH))
        )
            .observeOn(swingScheduler)
            .subscribe({
                when (it) {
                    is File -> println("Opened movie file : $it")
                    is Subtitles -> println("Opened subtitles : ${it.timedTexts.size} subtitles")
                }
                transport.setStatus("opened: ${EditorView.DEF_BASE_PATH}")
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    override fun onConfirmSave() {
        state.srtWriteFile?.let { saveWriteSrt(it) }
    }

    override fun onConfirmDontSave() {
        System.exit(0)
    }

    override fun onConfirmSaveAs() {
        saveAs()
    }
    // endregion

    // region TransportContract.StateListener
    override fun speed(speed: Float) {
        view.setMovieSpeed(speed)
    }
    // endregion

    // region Movie
    private fun setMovieFile(file: File) {
        openMovieSingle(file)
            .observeOn(swingScheduler)
            .subscribe({
                transport.setStatus("Opened movie : $file")
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { state.movieFile = it }
            .subscribeOn(pScheduler)
            .doOnSuccess { view.openMovie(it) }
            .subscribeOn(swingScheduler)
            .doOnSuccess {
                transport.setMovieTitle(it.name)
                transport.speed = 1f
            }
    }
    // endregion

    private fun processEvent(uiEvent: TransportContract.UiEvent) {
        when (uiEvent.uiEventType) {
            PLAY -> {
                view.play()
            }
            PAUSE -> {
                view.pause()
            }
            MUTE -> Unit
            SEEK -> {
                state.movieDurationSec?.apply { jumpTo((uiEvent.data as Float) * this) }
            }
            VOLUME_CHANGED -> {
                view.volume(uiEvent.data as Float)
            }
            MENU_FILE_OPEN_MOVIE -> {
                transport.showOpenDialog("Open Movie", state.movieFile?.parentFile) { file ->
                    setMovieFile(file)
                }
            }
            MENU_FILE_OPEN_SRT_READ -> {
                transport.showOpenDialog("Open SRT for read", state.movieFile?.parentFile) { file ->
                    openReadSrt(file)
                }
            }
            MENU_FILE_NEW_SRT_WRITE -> {
                state.srtWriteFile = null
                state.srtWrite = Subtitles(listOf())
                transport.setSrtWriteTitle("New")
            }
            MENU_FILE_OPEN_SRT_WRITE -> {
                state.srtWriteFile = null
                transport.showOpenDialog("Open SRT for write", state.movieFile?.parentFile) { file ->
                    openWriteSrt(file)
                }
            }
            MENU_FILE_SAVE_SRT -> {
                state.srtWriteFile?.apply { saveWriteSrt(this) } ?: saveAs()
            }
            MENU_FILE_SAVE_SRT_AS -> {
                saveAs()
            }
            MENU_FILE_EXIT -> {
                if (state.isDirty) {
                    view.showExitDialog()
                } else {
                    System.exit(0)
                }
            }
            MENU_VIEW_READ_SUBLIST -> {
                readSubs.showWindow()
            }
            MENU_VIEW_WRITE_SUBLIST -> {
                writeSubs.showWindow()
            }
            MENU_VIEW_EDIT_SUBLIST -> {
                subEdit.showWindow()
            }
            LOOP -> {
                if (!(uiEvent.data as Boolean)) {
                    state.loopEndSec = null
                    state.loopStartSec = null
                }
            }
            else -> Unit
        }
        println("EditPresenter: transport.event: ${uiEvent.uiEventType} -> ${uiEvent.data}")
    }

    private fun saveAs() {
        val currentDir = state.srtWriteFile?.parentFile ?: state.movieFile?.parentFile
        transport.showSaveDialog("Save SRT As ..", currentDir) { file ->
            saveWriteSrt(file)
        }
    }

    // region SRT read write
    private fun saveWriteSrt(file: File) {
        state.srtWrite?.let {
            srtInteractor.write(it, file)
                .subscribeOn(Schedulers.io())
                .observeOn(swingScheduler)
                .subscribe({
                    state.isDirty = false
                    state.srtWriteFile = file
                    transport.setStatus("subtitles saved to :- ${file.absolutePath}")
                }, { it.printStackTrace() })
                .also { disposables.add(it) }
        }
    }

    private fun openReadSrt(file: File) {
        srtReadOpenSingle(file)
            .observeOn(swingScheduler)
            .subscribe({
                transport.setStatus("opened Read SRT: ${it}")
            }, {
                it.printStackTrace()
            }).also { disposables.add(it) }
    }

    private fun srtReadOpenSingle(file: File): Single<Subtitles> {
        return srtOpenSingle(file)
            .subscribeOn(swingScheduler)
            .doOnSuccess {
                state.srtRead = it
                state.srtReadFile = file
                transport.setSrtReadTitle(file.name)
                readSubs.setList(it)
            }
    }

    private fun openWriteSrt(file: File) {
        srtWriteOpenSingle(file)
            .observeOn(swingScheduler)
            .subscribe({
                println("opened Read SRT: ${it}")
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    private fun srtWriteOpenSingle(file: File): Single<Subtitles> {
        return srtOpenSingle(file)
            .subscribeOn(swingScheduler)
            .doOnSuccess {
                state.srtWrite = it
                state.srtWriteFile = file
                transport.setSrtWriteTitle(file.name)
                writeSubs.setList(it)
            }
    }

    private fun srtOpenSingle(file: File) =
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())

    //endregion

    // region movie control
    private fun jumpTo(positionSec: Float) {
        println("jumpTo: $positionSec")
        // fixme subtitles aren't syncing up properly after jump
        readSubTracker.scanForPosition(positionSec)
        writeSubTracker.scanForPosition(positionSec)
        view.seekTo(positionSec)
    }

    private fun setLooping(fromSec: Float, toSec: Float) {
        println("setLooping: $fromSec -> $toSec")
        state.loopStartSec = fromSec
        state.loopEndSec = toSec
        transport.setLooping(true)
    }

    private fun checkLoopingPos(pos: Float) {
        state.loopEndSec?.let { loopEnd ->
            state.loopStartSec?.let { loopStart ->
                if (pos > loopEnd) {
                    jumpTo(loopStart)
                }
            }
        }
    }
    // endregion
}
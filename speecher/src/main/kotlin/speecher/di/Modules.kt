package speecher.di

import org.koin.dsl.module
import speecher.editor.EditorView
import speecher.editor.subedit.SubEditContract
import speecher.editor.subedit.SubEditPresenter
import speecher.editor.subedit.word_timeline.WordTimelineView
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportPresenter
import speecher.generator.GeneratorPresenter
import speecher.generator.movie.MoviePresenter
import speecher.generator.ui.SpeechContract
import speecher.generator.ui.SpeechPresenter
import speecher.generator.ui.sentence_list.SentenceListContract
import speecher.generator.ui.sentence_list.SentenceListPresenter
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.scheduler.SchedulerModule
import speecher.util.format.TimeFormatter
import speecher.util.subs.SubFinder
import speecher.util.subs.SubTracker
import speecher.util.wrapper.LogWrapper

object Modules {

    private val scopedModules = listOf(
        EditorView.viewModule,
        SentenceListPresenter.scope,
        TransportPresenter.scope,
        SubEditPresenter.scope,
        WordTimelineView.scope
    )

    private val subViewModules = module {
        factory<SentenceListContract.External> { SentenceListPresenter() }
        factory<TransportContract.External> { TransportPresenter() }
        factory<SubEditContract.External> { SubEditPresenter() }
        factory<SpeechContract.External> { SpeechPresenter(get()) }
    }

    private val generatorModules = listOf(
        MoviePresenter.scopeModule,
        GeneratorPresenter.module,
        SpeechPresenter.scope
    )

    private val utilModule = module {
        single { TimeFormatter() }
        factory { SubFinder() }
        factory { SubTracker() }
        factory { LogWrapper(get()) }
    }

    private val srtModule = module {
        factory { SrtInteractor(get(), get()) }
        factory { SrtFileReader(get()) }
        factory { SrtFileWriter(get()) }
        factory { SrtMapper() }
    }

    val allModules = listOf(utilModule)
        .plus(srtModule)
        .plus(subViewModules)
        .plus(scopedModules)
        .plus(SchedulerModule.module)
        .plus(generatorModules)
}
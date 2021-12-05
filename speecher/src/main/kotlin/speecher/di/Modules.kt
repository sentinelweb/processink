package speecher.di

import org.koin.dsl.module
import speecher.editor.EditorView
import speecher.editor.subedit.SubEditContract
import speecher.editor.subedit.SubEditPresenter
import speecher.editor.subedit.word_timeline.WordTimelineView
import speecher.editor.sublist.SubListContract
import speecher.editor.sublist.SubListPresenter
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportPresenter
import speecher.generator.GeneratorPresenter
import speecher.generator.bank.MovieBankCreator
import speecher.generator.movie.MovieCreator
import speecher.generator.osc.OscContract
import speecher.generator.ui.SpeechContract
import speecher.generator.ui.SpeechPresenter
import speecher.generator.ui.sentence_list.SentenceListContract
import speecher.generator.ui.sentence_list.SentenceListPresenter
import speecher.interactor.sentence.SentencesInteractor
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.scheduler.SchedulerModule
import speecher.util.format.FilenameFormatter
import speecher.util.format.TimeFormatter
import speecher.util.serialization.*
import speecher.util.subs.SubFinder
import speecher.util.subs.SubTracker
import speecher.util.wrapper.LogWrapper

object Modules {

    private val scopedModules = listOf(
        EditorView.viewModule,
        SubListPresenter.scope,
        TransportPresenter.scope,
        SubEditPresenter.scope,
        WordTimelineView.scope
    )

    private val subViewModules = module {
        factory<SubListContract.External> { SubListPresenter() }
        factory<TransportContract.External> { TransportPresenter() }
        factory<SubEditContract.External> { SubEditPresenter() }
        factory<SpeechContract.External> { SpeechPresenter(get()) }
        factory<SentenceListContract.External> { SentenceListPresenter() }
    }

    private val generatorModules = listOf(
        MovieCreator.scopeModule,
        GeneratorPresenter.module,
        SpeechPresenter.scope,
        SentenceListPresenter.scope,
        OscContract.scopeModule,
        module {
            factory { MovieBankCreator() }
            factory { MovieCreator() }
        }
    )

    val utilModule = module {
        single { TimeFormatter() }
        factory { SubFinder() }
        factory { SubTracker() }
        factory { LogWrapper(get()) }
        factory { FilenameFormatter() }
        factory { GsonSerializer(FileSerializer, FontSerializer, ColorSerializer).gson }
        single { JSON }
    }

    private val interactorModule = module {
        factory { SrtInteractor(get(), get()) }
        factory { SrtFileReader(get()) }
        factory { SrtFileWriter(get()) }
        factory { SrtMapper() }
        factory { SentencesInteractor(get()) }
    }

    val allModules = listOf(utilModule)
        .plus(interactorModule)
        .plus(subViewModules)
        .plus(scopedModules)
        .plus(SchedulerModule.module)
        .plus(generatorModules)
}
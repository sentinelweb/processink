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
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.scheduler.SchedulerModule
import speecher.util.format.TimeFormatter
import speecher.util.subs.SubFinder

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
    }

    private val utilModule = module {
        single { TimeFormatter() }
        factory { SubFinder() }
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
}
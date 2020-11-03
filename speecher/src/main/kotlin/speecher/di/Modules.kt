package speecher.di

import org.koin.dsl.module
import speecher.editor.EditorView
import speecher.editor.sublist.SubListContract
import speecher.editor.sublist.SubListPresenter
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportPresenter
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.util.format.TimeFormatter

object Modules {
    private val scopedModules = listOf(
        EditorView.viewModule,
        SubListPresenter.scope,
        TransportPresenter.scope
    )

    private val subViewModules = module {
        factory<SubListContract.External> { SubListPresenter() }
        factory<TransportContract.External> { TransportPresenter() }
    }

    private val utilModule = module {
        single { TimeFormatter() }
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
}
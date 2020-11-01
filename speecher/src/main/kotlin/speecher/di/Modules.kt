package speecher.di

import org.koin.dsl.module
import speecher.editor.EditorView
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.util.format.TimeFormatter

object Modules {
    private val scopedModules = listOf(
        EditorView.viewModule
    )

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
        .plus(scopedModules)
}
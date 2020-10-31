package speecher.interactor.srt

import io.reactivex.Completable
import io.reactivex.Observable
import speecher.domain.Subtitles
import java.io.File

class SrtInteractor constructor(
    private val reader: SrtFileReader,
    private val writer: SrtFileWriter
) {

    fun read(f: File): Observable<Subtitles> = Observable.fromCallable {
        reader.read(f)
    }

    fun write(subs: Subtitles, f: File) = Completable.fromCallable {
        writer.write(subs, f)
    }
}
package speecher.interactor.sentence

import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.json.Json
import java.io.File

class SentencesInteractor constructor(
    private val json: Json
) {

    fun openFile(f: File): Single<SentencesData> =
        Single.just(f)
            .map {
                json.decodeFromString(SentencesData.serializer(), it.readText())
            }

    fun saveFile(f: File, data: SentencesData): Completable =
        Single.just(f)
            .doOnSuccess {
                it.writeText(json.encodeToString(SentencesData.serializer(), data))
            }.ignoreElement()

}
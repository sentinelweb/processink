package cubes.util.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.awt.Color
import java.awt.Font
import java.io.File

class GsonSerializer constructor(
    private val fileSerializer: FileSerializer,
    private val fontSerializer: FontSerializer,
    private val colorSerializer: ColorSerializer
) {

    val gson: Gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .registerTypeAdapter(Font::class.java, object : TypeAdapter<Font>() {
            override fun write(out: JsonWriter, value: Font?) {
                value?.apply { out.value(fontSerializer.makeString(value)) }
                    ?: out.nullValue()
            }

            override fun read(inp: JsonReader): Font? =
                if (inp.peek() != JsonToken.NULL) {
                    fontSerializer.parseString(inp.nextString())
                } else {
                    inp.nextNull();null
                }
        })
        .registerTypeAdapter(File::class.java, object : TypeAdapter<File>() {
            override fun write(out: JsonWriter, value: File?) {
                value?.apply { out.value(fileSerializer.makeString(value)) }
                    ?: out.nullValue()
            }

            override fun read(inp: JsonReader): File? =
                if (inp.peek() != JsonToken.NULL) {
                    fileSerializer.parseString(inp.nextString())
                } else {
                    inp.nextNull();null
                }
        })
        .registerTypeAdapter(Color::class.java, object : TypeAdapter<Color>() {
            override fun write(out: JsonWriter, value: Color?) {
                value?.apply { out.value(colorSerializer.makeInt(value).toString()) }
                    ?: out.nullValue()
            }

            override fun read(inp: JsonReader): Color? =
                if (inp.peek() != JsonToken.NULL) {
                    colorSerializer.parseInt(inp.nextInt())
                } else {
                    inp.nextNull();null
                }
        })
        .create()

}
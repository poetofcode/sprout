package data.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.io.File
import kotlin.reflect.KProperty


@Serializable
data class PreferencesInfo(
    val preferences: Map<String, @kotlinx.serialization.Serializable(with = AnySerializer::class) Any>? = null
)

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = serializeAny(value)
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    private fun serializeAny(value: Any?): JsonElement = when (value) {
        is Map<*, *> -> {
            val mapContents = value.entries.associate { mapEntry ->
                mapEntry.key.toString() to serializeAny(mapEntry.value)
            }
            JsonObject(mapContents)
        }

        is List<*> -> {
            val arrayContents = value.map { listEntry -> serializeAny(listEntry) }
            JsonArray(arrayContents)
        }

        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        else -> JsonPrimitive(value.toString())
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }

        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }

        is JsonPrimitive -> element.toString()
    }
}


interface ContentProvider {

    fun provideContent(): String

    fun saveContent(content: String)

}

class FileContentProvider(
    val fileName: String,
    val relativePath: String,
) : ContentProvider {
    override fun provideContent(): String {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName").bufferedReader()
        return stream.use { it.readText() }
    }

    override fun saveContent(content: String) {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName")
        stream.printWriter().use {
            it.write(content)
        }
    }

}


interface PersistentStorage {

    fun save(key: String, param: Any)

    fun fetch(key: String): Any?

}

class ContentBasedPersistentStorage(
    private val contentPorvider: ContentProvider
) : PersistentStorage {

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    private val map: MutableMap<String, Any> by lazy {
        val content = try {
            contentPorvider.provideContent()
        } catch (e: Throwable) {
            "{}"
        }
        val map = json.decodeFromString<PreferencesInfo>(content).preferences?.toMutableMap() ?: mutableMapOf()
        map.toMap().toMutableMap()
    }


    override fun save(key: String, param: Any) {
        map[key] = param

        val str = json.encodeToString(PreferencesInfo(map))
        contentPorvider.saveContent(str)
    }

    override fun fetch(key: String): Any? {
        return map[key]
    }
}


inline operator fun <reified T : Any> PersistentStorage.getValue(nothing: Any?, property: KProperty<*>): T? {
    val properyName = property.name
    val res = fetch(properyName)
    return when (T::class) {
        String::class -> res?.toString() as? T
        Int::class -> res?.toString()?.toIntOrNull() as? T
        Boolean::class -> res?.toString()?.toBooleanStrictOrNull() as? T

        else -> null
    }
}

inline operator fun <reified T : Any> PersistentStorage.setValue(nothing: Any?, property: KProperty<*>, value: T?) {
    val propertyName = property.name
    value?.let {
        this.save(propertyName, value)
    }
}

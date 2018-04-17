package io.koto.json

import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.MalformedJsonException
import java.io.Reader
import java.io.StringReader

abstract class Json : MutableMap<String, Json>, MutableList<Json> {

    companion object {

        private fun JsonReader.parse(): Json {
            return when (peek()) {
                JsonToken.BEGIN_ARRAY -> parseArray()
                JsonToken.BEGIN_OBJECT -> parseObject()
                JsonToken.STRING -> Json.JString(nextString())
                JsonToken.NUMBER -> Json.JNumber(nextDouble())
                JsonToken.BOOLEAN -> Json.JBool(nextBoolean())
                JsonToken.NULL -> Json.JNull
                else -> throw MalformedJsonException("Token(${peek().name}) is not expected!")
            }
        }

        private fun JsonReader.parseObject(): Json.JObject {
            return Json.JObject().apply {
                beginObject()
                while (hasNext()) {
                    parsePair().let {
                        put(it.first, it.second)
                    }
                }
                endObject()
            }
        }

        private fun JsonReader.parsePair(): Pair<String, Json> {
            return nextName() to parse()
        }

        private fun JsonReader.parseArray(): Json.JArray {
            return Json.JArray().apply {
                beginArray()
                while (hasNext()) {
                    add(parse())
                }
                endArray()
            }
        }

        private fun jelement(obj: Any?): Json {
            return if (obj == null) {
                JNull
            } else when (obj) {
                is Number -> JNumber(obj)
                is Boolean -> JBool(obj)
                is String -> JString(obj)
                is Json -> obj
                else -> JString(toString())
            }
        }

        fun json(reader: Reader) = JsonReader(reader).parse()

        fun json(json: String) = json(StringReader(json))

        inline fun obj(block: JObject.() -> Unit) = JObject().apply { block() }

        fun arr(vararg objs: Any?) = JArray().apply {
            objs.forEach { add(jelement(it)) }
        }

    }

    fun isNull() = this === JNull
    fun isBool() = this is JBool
    fun isString() = this is JString
    fun isNumber() = this is JNumber
    fun isObject() = this is JObject
    fun isArray() = this is JArray

    open fun bool(): Boolean = throw TypeCastException()
    open fun string(): String = throw TypeCastException()
    open fun int(): Int = throw TypeCastException()
    open fun float(): Float = throw TypeCastException()
    open fun double(): Double = throw TypeCastException()
    open fun jnull(): JNull = throw TypeCastException()
    open fun obj(): MutableMap<String, Json> = throw TypeCastException()
    open fun arr(): MutableList<Json> = throw TypeCastException()
    abstract fun json(): String
    override fun toString(): String = json()


    //for interface MutableList
    override val size: Int get() = throw JsonParseException("illegal action for the element")

    override fun contains(element: Json): Boolean = throw JsonParseException("Can not do that for this Json element")
    override fun containsAll(elements: Collection<Json>): Boolean = throw JsonParseException("illegal action for the element")
    override fun get(index: Int): Json = throw JsonParseException("illegal action for the element")
    override fun indexOf(element: Json): Int = throw JsonParseException("illegal action for the element")
    override fun isEmpty(): Boolean = throw JsonParseException("illegal action for the element")
    override fun iterator(): MutableIterator<Json> = throw JsonParseException("illegal action for the element")
    override fun lastIndexOf(element: Json): Int = throw JsonParseException("illegal action for the element")
    override fun add(element: Json): Boolean = throw JsonParseException("illegal action for the element")
    override fun add(index: Int, element: Json): Unit = throw JsonParseException("illegal action for the element")
    override fun addAll(index: Int, elements: Collection<Json>): Boolean = throw JsonParseException("illegal action for the element")
    override fun addAll(elements: Collection<Json>): Boolean = throw JsonParseException("illegal action for the element")
    override fun clear(): Unit = throw JsonParseException("illegal action for the element")
    override fun listIterator(): MutableListIterator<Json> = throw JsonParseException("illegal action for the element")
    override fun listIterator(index: Int): MutableListIterator<Json> = throw JsonParseException("illegal action for the element")
    override fun remove(element: Json): Boolean = throw JsonParseException("illegal action for the element")
    override fun removeAll(elements: Collection<Json>): Boolean = throw JsonParseException("illegal action for the element")
    override fun removeAt(index: Int): Json = throw JsonParseException("illegal action for the element")
    override fun retainAll(elements: Collection<Json>): Boolean = throw JsonParseException("illegal action for the element")
    override fun set(index: Int, element: Json): Json = throw JsonParseException("illegal action for the element")
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Json> = throw JsonParseException("illegal action for the element")

    //for json array
    open operator fun set(index: Int, obj: Number) {
        set(index, JNumber(obj))
    }

    open operator fun set(index: Int, obj: String?) {
        set(index, if (obj != null) {
            JString(obj)
        } else {
            JNull
        })
    }

    open operator fun set(index: Int, obj: Boolean) {
        set(index, JBool(obj))
    }

    operator fun plusAssign(obj: Any?) {
        add(jelement(obj))
    }

    //for interface MutableMap
    override fun containsKey(key: String): Boolean = throw JsonParseException("illegal action for the element")

    override fun containsValue(value: Json): Boolean = throw JsonParseException("illegal action for the element")
    override fun get(key: String): Json = throw JsonParseException("illegal action for the element")
    override val entries: MutableSet<MutableMap.MutableEntry<String, Json>> get() = throw JsonParseException("illegal action for the element")
    override val keys: MutableSet<String> get() = throw JsonParseException("illegal action for the element")
    override val values: MutableCollection<Json> get() = throw JsonParseException("illegal action for the element")
    override fun put(key: String, value: Json): Json? = throw JsonParseException("illegal action for the element")
    override fun putAll(from: Map<out String, Json>): Unit = throw JsonParseException("illegal action for the element")
    override fun remove(key: String): Json? = throw JsonParseException("illegal action for the element")

    //for json object
    open operator fun set(key: String, obj: Number) {
        put(key, JNumber(obj))
    }

    open operator fun set(key: String, obj: String?) {
        put(key, if (obj != null) {
            JString(obj)
        } else {
            JNull
        })
    }

    open operator fun set(key: String, obj: Boolean) {
        put(key, JBool(obj))
    }

    object JNull : Json() {
        override fun jnull() = this
        override fun json(): String = "null"
    }

    class JBool(private val v: Boolean) : Json() {
        override fun bool(): Boolean = v
        override fun json(): String = v.toString()
    }

    class JNumber(private val v: Number) : Json() {
        override fun int(): Int = v.toInt()
        override fun float(): Float = v.toFloat()
        override fun double(): Double = v.toDouble()
        override fun json(): String = v.toString()
    }

    class JString(private val v: String) : Json() {
        override fun string(): String = v
        override fun json(): String = "\"$v\""
    }

    class JObject : Json() {
        override fun obj() = map
        private val map = mutableMapOf<String, Json>()
        override val size: Int get() = map.size
        override fun containsKey(key: String): Boolean = map.containsKey(key)
        override fun containsValue(value: Json): Boolean = map.containsValue(value)
        override fun get(key: String): Json = map[key] ?: throw JsonParseException("does not contain the key")
        override fun isEmpty(): Boolean = map.isEmpty()
        override val entries: MutableSet<MutableMap.MutableEntry<String, Json>> get() = map.entries
        override val keys: MutableSet<String> get() = map.keys
        override val values: MutableCollection<Json> get() = map.values
        override fun clear() = map.clear()
        override fun put(key: String, value: Json): Json? = map.put(key, value)
        override fun putAll(from: Map<out String, Json>) = map.putAll(from)
        override fun remove(key: String): Json? = map.remove(key)
        override fun json(): String = map.entries.joinToString(",", "{", "}") { "\"${it.key}\":${it.value}" }

        infix fun String.to(value: Number) = put(this, JNumber(value))
        infix fun String.to(value: String) = put(this, JString(value))
        infix fun String.to(value: Boolean) = put(this, JBool(value))
        infix fun String.to(value: Json) = put(this, value)
        infix fun String.to(value: JNull?) = put(this, JNull)
    }

    class JArray : Json() {
        override fun arr() = list
        private val list = mutableListOf<Json>()
        override val size: Int get() = list.size
        override fun contains(element: Json): Boolean = list.contains(element)
        override fun containsAll(elements: Collection<Json>): Boolean = list.containsAll(elements)
        override fun get(index: Int): Json = list[index]
        override fun indexOf(element: Json): Int = list.indexOf(element)
        override fun isEmpty(): Boolean = list.isEmpty()
        override fun iterator(): MutableIterator<Json> = list.iterator()
        override fun lastIndexOf(element: Json): Int = list.lastIndexOf(element)
        override fun add(element: Json): Boolean = list.add(element)
        override fun add(index: Int, element: Json) = list.add(index, element)
        override fun addAll(index: Int, elements: Collection<Json>): Boolean = list.addAll(index, elements)
        override fun addAll(elements: Collection<Json>): Boolean = list.addAll(elements)
        override fun clear() = list.clear()
        override fun listIterator(): MutableListIterator<Json> = list.listIterator()
        override fun listIterator(index: Int): MutableListIterator<Json> = list.listIterator(index)
        override fun remove(element: Json): Boolean = list.remove(element)
        override fun removeAll(elements: Collection<Json>): Boolean = list.removeAll(elements)
        override fun removeAt(index: Int): Json = list.removeAt(index)
        override fun retainAll(elements: Collection<Json>): Boolean = list.retainAll(elements)
        override fun set(index: Int, element: Json): Json = list.set(index, element)
        override fun subList(fromIndex: Int, toIndex: Int): MutableList<Json> = list.subList(fromIndex, toIndex)
        override fun json(): String = list.joinToString(",", "[", "]")
    }
}






package io.koto.json

fun json(json: String) = Json.parse(json)
fun obj(vararg pairs : Pair<String, Any?>) = Json.jsonObject(*pairs)
fun arr(vararg objs: Any?) = Json.jsonArray(*objs)

abstract class Json : MutableMap<String, Json>, MutableList<Json> {
    class JException(message: String?) : Exception(message)
    companion object {
        internal fun error(msg:String? = null) : Nothing = throw JException(msg)
        private fun jelement(obj:Any?) : Json {
            return if (obj == null) { JNull } else when(obj) {
                is Number -> JNumber(obj)
                is Boolean -> JBool(obj)
                is String -> JString(obj)
                is Json -> obj
                else -> JString(toString())
            }
        }

        fun parse(json: String) = JParser(json).parse()
        fun jsonObject(vararg pairs : Pair<String, Any?>) = JObject().apply {
            pairs.forEach { put(it.first, jelement(it.second)) }
        }

        fun jsonArray(vararg objs: Any?) = JArray().apply {
            objs.forEach { add(jelement(it)) }
        }
    }

    fun isNull() = this is JNull
    fun isBool() = this is JBool
    fun isString() = this is JString
    fun isNumber() = this is JNumber
    fun isObject() = this is JObject
    fun isArray() = this is JArray

    open fun bool() : Boolean = error()
    open fun string() : String = error()
    open fun int() : Int = error()
    open fun float() : Float = error()
    open fun double() : Double = error()
    open fun jnull() : JNull = error()
    open fun obj() : MutableMap<String, Json> = error()
    open fun arr() : MutableList<Json> = error()

    open fun json() : String = error()
    override fun toString(): String = json()


    //for interface MutableList
    override val size: Int get() = error()
    override fun contains(element: Json): Boolean = error()
    override fun containsAll(elements: Collection<Json>): Boolean  = error()
    override fun get(index: Int): Json = error()
    override fun indexOf(element: Json): Int = error()
    override fun isEmpty(): Boolean = error()
    override fun iterator(): MutableIterator<Json> = error()
    override fun lastIndexOf(element: Json): Int = error()
    override fun add(element: Json): Boolean = error()
    override fun add(index: Int, element: Json): Unit = error()
    override fun addAll(index: Int, elements: Collection<Json>): Boolean = error()
    override fun addAll(elements: Collection<Json>): Boolean = error()
    override fun clear(): Unit = error()
    override fun listIterator() : MutableListIterator<Json> = error()
    override fun listIterator(index: Int): MutableListIterator<Json> = error()
    override fun remove(element: Json): Boolean = error()
    override fun removeAll(elements: Collection<Json>): Boolean = error()
    override fun removeAt(index: Int): Json = error()
    override fun retainAll(elements: Collection<Json>): Boolean = error()
    override fun set(index: Int, element: Json): Json = error()
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Json> = error()

    //for json array
    open operator fun set(index: Int, obj: Number) {
        set(index, JNumber(obj))
    }
    open operator fun set(index: Int, obj: String?) {
        set(index, if(obj!=null){JString(obj)}else{JNull})
    }
    open operator fun set(index: Int, obj: Boolean) {
        set(index, JBool(obj))
    }

//    open fun add(obj: Number) {
//        add(JNumber(obj))
//    }
//    open fun add(obj: String?) {
//        add(if(obj!=null){JString(obj)}else{JNull})
//    }
//    open fun add(obj: Boolean) {
//        add(JBool(obj))
//    }
//    operator fun plusAssign(obj: Number) = add(obj)
//    operator fun plusAssign(obj: String?) = add(obj)
//    operator fun plusAssign(obj: Boolean) = add(obj)
    operator fun plusAssign(obj: Any?) { add(jelement(obj)) }

    //for interface MutableMap
//    override val size: Int = error()
    override fun containsKey(key: String): Boolean = error()
    override fun containsValue(value: Json): Boolean = error()
    override fun get(key: String): Json = error()
    //    override fun isEmpty(): Boolean = error()
    override val entries: MutableSet<MutableMap.MutableEntry<String, Json>> get() = error()
    override val keys: MutableSet<String> get() = error()
    override val values: MutableCollection<Json> get() = error()
    //    override fun clear(): Unit = error()
    override fun put(key: String, value: Json): Json? = error()
    override fun putAll(from: Map<out String, Json>): Unit = error()
    override fun remove(key: String): Json? = error()

    //for json object
    open operator fun set(key: String, obj: Number) {
        put(key, JNumber(obj))
    }
    open operator fun set(key: String, obj: String?) {
        put(key, if(obj!=null){JString(obj)}else{JNull})
    }
    open operator fun set(key: String, obj: Boolean) {
        put(key, JBool(obj))
    }

    object JNull : Json() {
        override fun jnull() = this
        override fun json(): String = "null"
    }

    class JBool(private val v:Boolean) : Json() {
        override fun bool(): Boolean = v
        override fun json(): String = v.toString()
    }

    class JNumber(private val v:Number) : Json() {
        override fun int(): Int = v.toInt()
        override fun float(): Float = v.toFloat()
        override fun double(): Double = v.toDouble()
        override fun json(): String = v.toString()
    }

    class JString(private val v:String) : Json() {
        override fun string(): String = v
        override fun json(): String = "\"$v\""
    }

    class JObject : Json() {
        override fun obj() = map
        private val map = mutableMapOf<String, Json>()
        override val size: Int get() = map.size
        override fun containsKey(key: String): Boolean = map.containsKey(key)
        override fun containsValue(value: Json): Boolean = map.containsValue(value)
        override fun get(key: String): Json = map[key]?: error()
        override fun isEmpty(): Boolean = map.isEmpty()
        override val entries: MutableSet<MutableMap.MutableEntry<String, Json>> get() = map.entries
        override val keys: MutableSet<String> get() = map.keys
        override val values: MutableCollection<Json> get() = map.values
        override fun clear() = map.clear()
        override fun put(key: String, value: Json): Json? = map.put(key, value)
        override fun putAll(from: Map<out String, Json>) = map.putAll(from)
        override fun remove(key: String): Json? = map.remove(key)
        override fun json(): String = map.entries.joinToString(",", "{", "}") { "\"${it.key}\":${it.value}" }
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
        override fun listIterator() : MutableListIterator<Json> = list.listIterator()
        override fun listIterator(index: Int): MutableListIterator<Json> = list.listIterator(index)
        override fun remove(element: Json): Boolean = list.remove(element)
        override fun removeAll(elements: Collection<Json>): Boolean = list.removeAll(elements)
        override fun removeAt(index: Int): Json = list.removeAt(index)
        override fun retainAll(elements: Collection<Json>): Boolean = list.retainAll(elements)
        override fun set(index: Int, element: Json): Json = list.set(index, element)
        override fun subList(fromIndex: Int, toIndex: Int): MutableList<Json> = list.subList(fromIndex, toIndex)
        override fun json(): String = list.joinToString(",","[","]")
    }

    private class JParser(val json:String) {
        private var at = 0
        private fun next() = json[at++]
        private val c : Char
            get() = json[at]
        private fun s(n:Int) : String {
            return json.substring(at until at+n)
        }
        private val spaces = listOf(' ', '\t', '\b', '\n', 'r')
        private fun escapeSpace() {
            while (c in spaces) {
                next()
            }
        }
        private fun isNext(str:String):Boolean {
            val v = try { s(str.length) == str } catch (e: Throwable) { false }
            at += str.length
            return v
        }
        private fun parseNull() : JNull {
            if (isNext("null")) {
                return JNull
            } else {
                error()
            }
        }
        private fun parseTrue() : Boolean {
            if (isNext("true")) {
                return true
            } else {
                error()
            }
        }
        private fun parseFalse() : Boolean {
            if (isNext("false")) {
                return false
            } else {
                error()
            }
        }
        private val escapes = mapOf(
                'b' to '\b',
                'n' to '\n',
                't' to '\t',
                'r' to '\r',
                '\"' to '\"',
                '\\' to '\\'
        )
        private fun parseString() : String {
            return buildString {
                try {
                    next()
                    while (c!='\"') {
                        if (c == '\\') {
                            next()
                            if (c == 'u') {
                                appendCodePoint(s(4).toInt(16))
                            } else {
                                append(escapes[c])
                            }
                        } else {
                            append(c)
                        }
                        next()
                    }
                    next()
                } catch (e:Throwable) {
                    error()
                }
            }
        }
        private fun parseDigit() : String {
            return buildString {
                do {
                    append(c)
                    next()
                } while (c in '0'..'9')
            }
        }
        private fun parseNumber() : Number {
            return buildString {
                if (c=='+' || c=='-') {
                    append(c)
                    next()
                }
                if (c in '0'..'9') {
                    append(parseDigit())
                    if (c == '.') {
                        append(c)
                        next()
                        if (c in '0'..'9') {
                            append(parseDigit())
                        }
                    }
                    if (c == 'e' || c == 'E') {
                        append(c)
                        next()
                        if (c == '+' || c == '-') {
                            append(c)
                            next()
                        }
                        if (c in '0'..'9') {
                            append(parseDigit())
                        } else {
                            error()
                        }
                    }
                } else {
                    error()
                }
            }.toDouble()
        }

        private fun parseArray():JArray {
            return JArray().apply {
                next()
                escapeSpace()
                if (c != ']') {
                    loop@ while (true) {
                        add(parseValue())
                        escapeSpace()
                        when (next()) {
                            ',' -> continue@loop
                            ']' -> break@loop
                            else -> error()
                        }
                    }
                }
            }
        }
        private fun parsePair() : Pair<String, Json> {
            escapeSpace()
            if (c != '\"') error()
            val key = parseString()
            escapeSpace()
            if (next() != ':') error()
            val value = parseValue()
            return key to value
        }
        private fun parseObject():JObject {
            return JObject().apply {
                next()
                escapeSpace()
                if (c != '}') {
                    loop@ while (true) {
                        parsePair().let { put(it.first, it.second) }
                        escapeSpace()
                        when (next()) {
                            ',' -> continue@loop
                            '}' -> break@loop
                            else -> error()
                        }
                    }
                }
            }
        }
        private fun parseValue() : Json {
            escapeSpace()
            return when(c) {
                '{'->parseObject()
                '['->parseArray()
                '\"'->JString(parseString())
                't'->JBool(parseTrue())
                'f'->JBool(parseFalse())
                'n'->parseNull()
                else->{
                    if (c=='-'||c=='+'||c in '0'..'9') {
                        JNumber(parseNumber())
                    } else {
                        error()
                    }
                }
            }
        }
        fun parse():Json {
            escapeSpace()
            return when(c) {
                '{' -> parseObject()
                '[' -> parseArray()
                else -> error()
            }
        }
    }
}






package io.koto.example
import io.koto.json.Json
import io.koto.json.Json.Companion.arr
import io.koto.json.Json.Companion.json
import io.koto.json.Json.Companion.obj

fun main(args: Array<String>) {

    //create json object
    val jsonObject = obj {
        "name" to "koto-json"
        "version" to 1.0
        "flag" to true
    }

    //create json array
    val jsonArray = arr("koto-json", 1.0, true)

    //combination of both
    val json1 = obj {
        "name" to "koto-json"
        "version" to 1.0
        "array" to arr("string", 1.0, false, obj {
                    "value" to "1"
                    "number" to 2
                }
        )
    }

    val name = json1["name"].string()
    val version = json1["version"].double()
    val value = json1["array"][3]["value"].string()
    val jarray = json1["array"]
    val jobject = jarray[3]
    jarray += obj {
        "test" to 1
    }
    val jarr1 = jarray.filter<Json> {
        it.isObject()
    }
    val jobj1 = jobject.entries.removeIf {
        it.value.isNumber()
    }

    //serializers
    val json_str = jobj1.toString()

    //deserializers
    val json_obj = json(json_str)
}
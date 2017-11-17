package io.koto.example

import io.koto.json.*

fun main(args: Array<String>) {

    //create json object
    val jsonObject = obj(
            "name" to "koto-json",
            "version" to 1.0,
            "flag" to true
    )

    //create json array
    val jsonArray = arr("koto-json", 1.0, true)

    //combination of both
    val json = obj(
            "name" to "koto-json",
            "version" to 1.0,
            "array" to arr(
                    "string",
                    1.0,
                    false,
                    obj(
                            "value" to "1",
                            "number" to 2
                    )
            )
    )

    val name = json["name"].string()
    val version = json["version"].double()
    val value = json["array"][3]["value"].string()
    val jarray = json["array"]
    val jobject = jarray[3]
    jarray += obj("test" to 1)
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
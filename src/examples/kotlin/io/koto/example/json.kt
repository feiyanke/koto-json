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

    var a = json("""   [ "111"  ,  true  ,  false,  null,null,123,  -342.12  , -456.1e2   ] """)
    a = json("""   {   "11  22":1  , "231"   :  {  ""  : true }  ,  "tt"  :  [   1,2,3,4,5]              } """)
//    val v = a["11  22"]
    a = obj(
            "a" to 1,
            "b" to 3,
            "c" to true,
            "d" to false,
            "e" to "111",
            "f" to null,
            "aa" to obj(
                    "1" to 1,
                    "2" to 2
            ),
            "bb" to arr("1", 2, "3", obj(
                    "3" to true,
                    "4" to null
            ))
    )
    val b = a.toString()
    println(b)
}
package io.koto.example

import io.koto.common.Json.Companion.array
import io.koto.common.Json.Companion.obj
import io.koto.common.Json.Companion.parse

fun main(args: Array<String>) {

    var a = parse("""   [ "111"  ,  true  ,  false,  null,null,123,  -342.12  , -456.1e2   ] """)
    a = parse("""   {   "11  22":1  , "231"   :  {  ""  : true }  ,  "tt"  :  [   1,2,3,4,5]              } """)
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
            "bb" to array("1", 2, "3", obj(
                    "3" to true,
                    "4" to null
            ))
    )
    val b = a.toString()
    println(b)
}
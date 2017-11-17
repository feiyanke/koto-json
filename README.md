# koto-json
a concise json library of Kotlin

[![jitpack](https://jitpack.io/v/feiyanke/koto-json.svg)](https://jitpack.io/#feiyanke/koto-json)

## Dependecies

**Step 1**. Add the JitPack repository to your build file

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Step 2**. Add the dependency


```gradle
dependencies {
  compile 'com.github.feiyanke:koto-json:0.1'
}
```
## Usage

### Create Json

```kotlin
import io.koto.json.*

//create json object
val json_object = obj(
        "name" to "koto-json",
        "version" to 1.0,
        "flag" to true
)

//create json array
val json_array = arr("koto-json", 1.0, true)

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
```

### Manipulate Json

```kotlin
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
```

### Json (de)serializers
```kotlin
//serializers
val json_str = jobj1.toString()

//deserializers
val json_obj = json(json_str)
}
```


package com.example.landtech.domain.utils

import android.location.Location
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

class LocationSerializer : JsonSerializer<Location>, JsonDeserializer<Location> {

    override fun serialize(src: Location?, typeOfSrc: java.lang.reflect.Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        src?.let {
            jsonObject.addProperty("latitude", src.latitude)
            jsonObject.addProperty("longitude", src.longitude)
            jsonObject.addProperty("accuracy", src.accuracy)
            jsonObject.addProperty("provider", src.provider)
        }
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: java.lang.reflect.Type?, context: JsonDeserializationContext?): Location {
        val jsonObject = json?.asJsonObject
        val location = Location("")
        jsonObject?.let {
            location.latitude = it.get("latitude").asDouble
            location.longitude = it.get("longitude").asDouble
            location.accuracy = it.get("accuracy").asFloat
            location.provider = it.get("provider").asString
        }
        return location
    }
}
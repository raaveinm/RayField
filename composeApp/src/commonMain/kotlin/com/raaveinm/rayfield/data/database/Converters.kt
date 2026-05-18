package com.raaveinm.rayfield.data.database

import androidx.room3.TypeConverter
import com.raaveinm.rayfield.data.xray.types.XrayKeyPair
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromXrayKeyPair(value: XrayKeyPair?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toXrayKeyPair(value: String?): XrayKeyPair? {
        return value?.let { Json.decodeFromString(it) }
    }
}

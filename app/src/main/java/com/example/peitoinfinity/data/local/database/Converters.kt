package com.example.peitoinfinity.data.local.database

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @ColumnTypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @ColumnTypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else json.decodeFromString(value)
}

package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.EngineerDb

data class EngineerDto(
    val guid: String,
    val name: String
) {
    fun toDatabaseModel() = EngineerDb(guid, name)
}

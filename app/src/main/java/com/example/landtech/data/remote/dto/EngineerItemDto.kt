package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.EngineersOrderItemDb

data class EngineerItemDto(
    val engineerId: String,
    val orderId: String
) {
    fun toDatabaseModel() = EngineersOrderItemDb(engineerId, orderId)
}

package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.UsedPartsItemDb

data class UsedPartsItemDto(
    val id: String,
    val clientUhm: String,
    val code: String,
    val name: String,
    val number: String,
    val quantity: Double
) {
    fun toDatabaseModel(orderId: String) = UsedPartsItemDb(id = id, clientUhm, code, name, number, quantity, orderId)
}
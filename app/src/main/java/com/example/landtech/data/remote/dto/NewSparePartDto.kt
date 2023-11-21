package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.NewSparePartDb

data class NewSparePartDto(
    val id: String,
    val orderId: String,
    val code: String,
    val name: String,
    val quantity: Double,
    var isDiagnose: Boolean
) {
    fun toDatabaseModel() = NewSparePartDb(id, orderId, name, code, quantity, isDiagnose)
}

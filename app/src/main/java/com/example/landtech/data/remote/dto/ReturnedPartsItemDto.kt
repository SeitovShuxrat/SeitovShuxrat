package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.ReturnedPartsItemDb
import com.example.landtech.domain.utils.toDate

data class ReturnedPartsItemDto(
    val id: String,
    val warehouse: String,
    val code: String,
    val date: String,
    val name: String,
    val number: String,
    val returned: Double,
    val received: Double,
) {
    fun toDatabaseModel(orderId: String) = ReturnedPartsItemDb(
        id = id,
        warehouse = warehouse,
        code = code,
        date = date.toDate(),
        name = name,
        number = number,
        returned = returned,
        received = received,
        orderId = orderId
    )

}
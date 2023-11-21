package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.ServiceItemDb
import com.example.landtech.domain.utils.toDateOnly

data class ServiceItemDto(
    val id: String,
    val date: String,
    val time: String,
    val workType: String,
    val measureUnit: String,
    val quantity: Double,
    val engineer: String,
    val explotationObject: String
) {
    fun toDatabaseModel(orderId: String) = ServiceItemDb(
        id = id,
        date = date.toDateOnly(),
        time = time,
        workType = workType,
        measureUnit = measureUnit,
        quantity = quantity,
        engineer = engineer,
        autoGN = explotationObject,
        orderId = orderId
    )
}

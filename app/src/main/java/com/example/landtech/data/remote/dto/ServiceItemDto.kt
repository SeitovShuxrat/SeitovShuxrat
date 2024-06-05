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
    val explotationObject: String,
    var ended: Boolean = false,
    var dateStart: String = "",
    var dateEnd: String = "",
    var byCurrentUser: Boolean = false,
    val engineerName: String = "",
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
        orderId = orderId,
        ended = ended,
        dateStart = dateStart,
        dateEnd = dateEnd,
        byCurrentUser = byCurrentUser
    )
}

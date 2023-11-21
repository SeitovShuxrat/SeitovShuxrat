package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.ReceivedPartsItemDb
import com.example.landtech.domain.utils.toDate

data class ReceivedPartsItemDto(
    val id: String,
    var warehouse: String,
    var code: String,
    var date: String,
    var name: String,
    var number: String,
    var needed: Double,
    var inTransit: Double,
    var received: Double,
    var beingPaid: Double,
    var rowId: String
) {
    fun toDatabaseModel(orderId: String) = ReceivedPartsItemDb(
        id = id,
        warehouse = warehouse,
        code = code,
        date = date.toDate(),
        name = name,
        number = number,
        needed = needed,
        inTransit = inTransit,
        received = received,
        beingPaid = beingPaid,
        orderId = orderId,
        rowId = rowId
    )
}
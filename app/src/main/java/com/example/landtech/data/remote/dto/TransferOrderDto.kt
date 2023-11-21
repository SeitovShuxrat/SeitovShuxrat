package com.example.landtech.data.remote.dto

import com.example.landtech.data.database.models.TransferOrderDb
import com.example.landtech.domain.utils.toDate
import java.util.Date

data class TransferOrderDto(
    var id: String,
    var orderId: String,
    var number: String,
    var date: String,
) {
    fun toDatabaseModel() = TransferOrderDb(
        id = id,
        number = number,
        date = date.toDate() ?: Date(),
        orderId = orderId
    )
}


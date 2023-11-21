package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.TransferOrderDb
import com.example.landtech.data.remote.dto.TransferOrderDto
import com.example.landtech.domain.utils.toDate
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class TransferOrder(
    var id: String,
    var number: String,
    var date: String,
    var orderId: String,
    var needToCreate: Boolean = false,
    var isCreated: Boolean = false
) : Parcelable {
    fun toDatabaseModel() =
        TransferOrderDb(
            id = id,
            number = number,
            date = date.toDate() ?: Date(),
            orderId = orderId,
            needToCreate = needToCreate,
            isCreated = isCreated
        )

    fun toDtoModel() = TransferOrderDto(id = id, orderId = orderId, number = number, date = date)
}

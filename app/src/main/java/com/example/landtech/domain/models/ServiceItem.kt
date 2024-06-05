package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.ServiceItemDb
import com.example.landtech.domain.utils.toDateOnly
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ServiceItem(
    var id: String = UUID.randomUUID().toString(),
    var date: String = "",
    var time: String = "",
    var dateStart: String = "",
    var dateEnd: String = "",
    var workType: String = "",
    var measureUnit: String = "",
    var quantity: Double = 0.0,
    var engineer: Engineer = Engineer(),
    var autoGN: String = "",
    var rowNum: Int = 1,
    var isLaborCost: Boolean = false,
    var ended: Boolean = false,
    var byCurrentUser: Boolean = false
) : Parcelable {
    fun toDatabaseModel(orderId: String) =
        ServiceItemDb(
            id = id,
            date = date.toDateOnly(),
            time = time,
            workType = workType,
            measureUnit = measureUnit,
            quantity = quantity,
            engineer = engineer.id,
            autoGN = autoGN,
            isLaborCost = isLaborCost,
            orderId = orderId,
            ended = ended,
            dateStart = dateStart,
            dateEnd = dateEnd,
            byCurrentUser = byCurrentUser
        )
}

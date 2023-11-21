package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.ReceivedPartsItemDb
import com.example.landtech.domain.utils.toDate
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ReceivedPartsItem(
    var id: String = UUID.randomUUID().toString(),
    var warehouse: String = "",
    var code: String = "",
    var date: String = "",
    var name: String = "",
    var number: String = "",
    var needed: Double = 0.0,
    var inTransit: Double = 0.0,
    var received: Double = 0.0,
    var beingPaid: Double = 0.0,
    var rowId: String = ""
) : Parcelable {
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

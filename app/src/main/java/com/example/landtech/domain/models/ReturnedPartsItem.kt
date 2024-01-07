package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.ReturnedPartsItemDb
import com.example.landtech.domain.utils.toDate
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ReturnedPartsItem(
    var id: String = UUID.randomUUID().toString(),
    var warehouse: String = "",
    var code: String = "",
    var date: String = "",
    var name: String = "",
    var number: String = "",
    var returned: Double = 0.0,
    var received: Double = 0.0,
    var isDeletable: Boolean = true,
    var maxQuantity: Double = 0.0   ,
    var isSaved: Boolean = false
) : Parcelable {
    fun toDatabaseModel(orderId: String) =
        ReturnedPartsItemDb(
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

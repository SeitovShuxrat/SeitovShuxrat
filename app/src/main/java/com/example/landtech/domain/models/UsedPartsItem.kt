package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.UsedPartsItemDb
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class UsedPartsItem(
    var id: String = UUID.randomUUID().toString(),
    var clientUhm: String = "",
    var code: String = "",
    var name: String = "",
    var number: String = "",
    var quantity: Double = 0.00,
    var quantityWarehouse: Double = 0.00,
) : Parcelable {
    fun toDatabaseModel(orderId: String) =
        UsedPartsItemDb(
            id = id,
            clientUhm = clientUhm,
            code = code,
            name = name,
            number = number,
            quantity = quantity,
            orderId = orderId
        )
}

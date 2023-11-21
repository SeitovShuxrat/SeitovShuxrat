package com.example.landtech.domain.models

import android.os.Parcelable
import com.example.landtech.data.database.models.EngineersOrderItemDb
import kotlinx.parcelize.Parcelize

@Parcelize
data class EngineersOrderItem(
    val engineerId: String,
    val orderId: String
) : Parcelable {
    fun toDatabaseModel() = EngineersOrderItemDb(engineerId, orderId)
}

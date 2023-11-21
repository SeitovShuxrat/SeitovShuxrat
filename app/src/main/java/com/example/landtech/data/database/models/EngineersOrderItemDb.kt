package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.EngineerItemDto
import com.example.landtech.domain.models.EngineersOrderItem

@Entity(
    tableName = "engineers_order_items",
    primaryKeys = ["engineerId", "orderId"],
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"]
    )],
    indices = [Index("orderId")]
)
data class EngineersOrderItemDb(
    var engineerId: String,
    var orderId: String
) {
    fun toDomainModel() = EngineersOrderItem(engineerId, orderId)
    fun toDtoModel() = EngineerItemDto(engineerId, orderId)
}

package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.UsedPartsItemDto
import com.example.landtech.domain.models.UsedPartsItem
import java.util.UUID


@Entity(
    tableName = "used_parts_items",
    primaryKeys = ["id", "orderId"],
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )],
    indices = [Index("orderId")]
)
data class UsedPartsItemDb(
    var id: String = UUID.randomUUID().toString(),
    var clientUhm: String = "",
    var code: String = "",
    var name: String = "",
    var number: String = "",
    var quantity: Double = 0.00,
    var orderId: String
) {
    fun toDomainModel() = UsedPartsItem(id, clientUhm, code, name, number, quantity)
    fun toDtoModel() = UsedPartsItemDto(id, clientUhm, code, name, number, quantity)
}

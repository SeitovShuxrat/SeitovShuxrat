package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.ReceivedPartsItemDto
import com.example.landtech.domain.models.ReceivedPartsItem
import com.example.landtech.domain.utils.makeString
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "received_parts_items",
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
data class ReceivedPartsItemDb(
    var id: String = UUID.randomUUID().toString(),
    var warehouse: String = "",
    var code: String = "",
    var date: Date? = null,
    var name: String = "",
    var number: String = "",
    var needed: Double = 0.0,
    var inTransit: Double = 0.0,
    var received: Double = 0.0,
    var beingPaid: Double = 0.0,
    var rowId: String = "",
    var orderId: String
) {
    fun toDomainModel() = ReceivedPartsItem(
        id,
        warehouse,
        code,
        date?.makeString() ?: "",
        name,
        number,
        needed,
        inTransit,
        received,
        beingPaid,
        rowId
    )

    fun toDtoModel() = ReceivedPartsItemDto(
        id,
        warehouse,
        code,
        date?.makeString() ?: "",
        name,
        number,
        needed,
        inTransit,
        received,
        beingPaid,
        rowId
    )
}

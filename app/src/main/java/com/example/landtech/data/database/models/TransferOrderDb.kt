package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.TransferOrderDto
import com.example.landtech.domain.models.TransferOrder
import com.example.landtech.domain.utils.makeString
import java.util.Date

@Entity(
    tableName = "transfer_order",
    primaryKeys = ["id", "orderId"],
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
    )],
    indices = [Index("orderId")]
)
data class TransferOrderDb(
    var id: String,
    var number: String,
    var date: Date,
    var orderId: String,
    var needToCreate: Boolean = false,
    var isCreated: Boolean = false
) {
    fun toDomainModel() = TransferOrder(
        id = id,
        number = number,
        date = date.makeString(),
        orderId = orderId,
        needToCreate = needToCreate,
        isCreated = isCreated
    )

    fun toDtoModel() = TransferOrderDto(
        id = id,
        number = number,
        orderId = orderId,
        date = date.makeString(),
    )
}

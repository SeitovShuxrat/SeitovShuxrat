package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.ReturnedPartsItemDto
import com.example.landtech.domain.models.ReturnedPartsItem
import com.example.landtech.domain.utils.makeString
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "returned_parts_items",
    primaryKeys = ["id", "orderId"],
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )], indices = [Index("orderId")]
)
data class ReturnedPartsItemDb(
    var id: String = UUID.randomUUID().toString(),
    var warehouse: String = "",
    var code: String = "",
    var date: Date? = null,
    var name: String = "",
    var number: String = "",
    var returned: Double = 0.0,
    var received: Double = 0.0,
    var orderId: String
) {
    fun toDomainModel() = ReturnedPartsItem(
        id,
        warehouse,
        code,
        date?.makeString() ?: "",
        name,
        number,
        returned,
        received,
        false,
        isSaved = true
    )

    fun toDtoModel() = ReturnedPartsItemDto(
        id,
        warehouse,
        code,
        date?.makeString() ?: "",
        name,
        number,
        returned,
        received
    )
}

package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.domain.models.NewSparePart
import java.util.UUID

@Entity(
    "new_spare_part",
    primaryKeys = ["id", "orderId"],
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
    )],
    indices = [Index("orderId")]
)
data class NewSparePartDb(
    var id: String = UUID.randomUUID().toString(),
    var orderId: String,
    var name: String,
    var code: String,
    var quantity: Double,
    var isDiagnose: Boolean
) {
    fun toDomainModel() = NewSparePart(id, name, code, quantity, isDiagnose)
    fun toDtoModel() = NewSparePartDto(id, orderId, code, name, quantity, isDiagnose)
}


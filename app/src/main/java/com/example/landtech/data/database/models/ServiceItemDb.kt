package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.landtech.domain.models.ServiceItem
import com.example.landtech.domain.utils.makeString
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "service_items",
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )],
    indices = [Index("orderId")]
)
data class ServiceItemDb(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var date: Date? = null,
    var time: String = "",
    var workType: String = "",
    var measureUnit: String = "",
    var quantity: Double = 0.0,
    var engineer: String = "",
    var autoGN: String = "",
    var rowNum: Int = 1,
    var orderId: String,
    var isLaborCost: Boolean = false
)
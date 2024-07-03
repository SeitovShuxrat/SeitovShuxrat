package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = EngineerDb::class,
        parentColumns = ["id"],
        childColumns = ["engineerId"],
    )],
    indices = [Index("engineerId")]
)
data class OrderDb(
    @PrimaryKey
    var orderId: String = UUID.randomUUID().toString(),
    var number: String = "",
    var date: Date = Date(),
    var client: String = "",
    var engineerId: String? = null,
    var workType: String = "",
    var startDate: Date? = null,
    var machine: String = "",
    var locationStartLat: Double = 0.0,
    var locationStartLng: Double = 0.0,
    var locationLat: Double = 0.0,
    var locationLng: Double = 0.0,
    var sn: String = "",
    var ln: String = "",
    var en: String = "",
    var driveTime: Double = 0.0,
    var driveTimeEnd: Double = 0.0,
    var driveStartDate: Date? = null,
    var driveEndDate: Date? = null,
    var typeOrder: String = "",
    var status: String = "",
    var workStartDate: Date? = null,
    var workEndDate: Date? = null,
    var tripEndDate: Date? = null,
    var tripEndLat: Double = 0.0,
    var tripEndLng: Double = 0.0,
    var imageUri: String? = null,
    var problemDescription: String = "",
    var workDescription: String = "",
    var quickReport: String = "",
    var isModified: Boolean = false,
    var needToCreateGuaranteeOrder: Boolean = false,
    var workHasNoGuarantee: Boolean = false,
    var clientRejectedToSign: Boolean = false,
    var partsAreReceived: Boolean = false,
    var isMainUser: Boolean = true,
    var workStarted: Boolean = false,
    var isInEngineersList: Boolean = false,
//    val services: MutableList<ServiceItem> = mutableListOf(),
//    val usedParts: MutableList<UsedPartsItem> = mutableListOf(),
//    val receivedParts: MutableList<ReceivedPartsItem> = mutableListOf(),
//    val returnedParts: MutableList<ReturnedPartsItem> = mutableListOf(),

)

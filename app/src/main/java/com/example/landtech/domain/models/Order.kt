package com.example.landtech.domain.models

import android.net.Uri
import android.os.Parcelable
import com.example.landtech.data.database.models.OrderDb
import com.example.landtech.data.remote.dto.TransferOrderDto
import com.example.landtech.domain.utils.toDate
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Order(
    var id: String = UUID.randomUUID().toString(),
    val number: String = "",
    val date: String = "",
    val client: String = "",
    var engineer: Engineer = Engineer(),
    val workType: String = "",
    var startDate: String = "",
    val machine: String = "",
    var locationStartLat: Double = 0.0,
    var locationStartLng: Double = 0.0,
    var locationLat: Double = 0.0,
    var locationLng: Double = 0.0,
    val sn: String = "",
    val ln: String = "",
    val en: String = "",
    var driveTime: Double = 0.0,
    var driveStartDate: String = "",
    var driveEndDate: String = "",
    var typeOrder: String = "",
    var status: String = "",
    var workStartDate: String = "",
    var workEndDate: String = "",
    var tripEndDate: String = "",
    var tripEndLat: Double = 0.0,
    var tripEndLng: Double = 0.0,
    var imageUri: Uri? = null,
    var problemDescription: String = "",
    var workDescription: String = "",
    var quickReport: String = "",
    val services: MutableList<ServiceItem> = mutableListOf(),
    val usedParts: MutableList<UsedPartsItem> = mutableListOf(),
    val receivedParts: MutableList<ReceivedPartsItem> = mutableListOf(),
    val returnedParts: MutableList<ReturnedPartsItem> = mutableListOf(),
    val engineersItems: MutableList<EngineersOrderItem> = mutableListOf(),
    val newSpareParts: MutableList<NewSparePart> = mutableListOf(),
    var needToCreateGuaranteeOrder: Boolean = false,
    var workHasNoGuarantee: Boolean = false,
    var isModified: Boolean = false,
    var clientRejectedToSign: Boolean = false,
    var partsAreReceived: Boolean = false,
    var isMainUser: Boolean = false,
    var workStarted: Boolean = false
) : Parcelable {

    fun toDatabaseModel() = OrderDb(
        orderId = id,
        number = number,
        date = date.toDate() ?: Date(),
        client = client,
        engineerId = engineer.id.ifBlank { null },
        workType = workType,
        startDate = startDate.toDate(),
        machine = machine,
        locationStartLat = locationStartLat,
        locationStartLng = locationStartLng,
        locationLat = locationLat,
        locationLng = locationLng,
        sn = sn,
        ln = ln,
        en = en,
        driveTime = driveTime,
        driveStartDate = driveStartDate.toDate(),
        driveEndDate = driveEndDate.toDate(),
        typeOrder = typeOrder,
        status = status,
        workStartDate = workStartDate.toDate(),
        workEndDate = workEndDate.toDate(),
        tripEndDate = tripEndDate.toDate(),
        tripEndLat = tripEndLat,
        tripEndLng = tripEndLng,
        imageUri = imageUri?.toString(),
        problemDescription = problemDescription,
        workDescription = workDescription,
        quickReport = quickReport,
        isModified = isModified,
        needToCreateGuaranteeOrder = needToCreateGuaranteeOrder,
        workHasNoGuarantee = workHasNoGuarantee,
        clientRejectedToSign = clientRejectedToSign,
        partsAreReceived = partsAreReceived,
        isMainUser = isMainUser,
        workStarted = workStarted
    )
}

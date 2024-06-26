package com.example.landtech.data.database.models

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Relation
import com.example.landtech.data.remote.dto.OrderDto
import com.example.landtech.domain.models.Engineer
import com.example.landtech.domain.models.Order
import com.example.landtech.domain.models.ReceivedPartsItem
import com.example.landtech.domain.utils.makeString

data class OrderAggregate(
    @Embedded
    val order: OrderDb,

    @Relation(
        parentColumn = "engineerId",
        entityColumn = "id"
    )
    val engineer: EngineerDb?,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val receivedPartsItems: List<ReceivedPartsItemDb>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val returnedPartsItems: List<ReturnedPartsItemDb>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = ServiceItemDb::class
    )
    val serviceItems: List<ServiceItemAggregate>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val usedPartsItemDb: List<UsedPartsItemDb>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = EngineersOrderItemDb::class
    )
    val engineersItems: List<EngineersOrderItemAggregate>,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val newSpareParts: List<NewSparePartDb>,

    ) {
    fun toDomainModel(): Order {
        return Order(
            id = order.orderId,
            number = order.number,
            date = order.date.makeString(),
            client = order.client,
            engineer = engineer?.toDomainModel() ?: Engineer(),
            workType = order.workType,
            startDate = order.startDate?.makeString() ?: "",
            machine = order.machine,
            locationStartLat = order.locationStartLat,
            locationStartLng = order.locationStartLng,
            locationLat = order.locationLat,
            locationLng = order.locationLng,
            sn = order.sn,
            ln = order.ln,
            en = order.en,
            driveTime = order.driveTime,
            driveTimeEnd = order.driveTimeEnd,
            driveStartDate = order.driveStartDate?.makeString() ?: "",
            driveEndDate = order.driveEndDate?.makeString() ?: "",
            typeOrder = order.typeOrder,
            status = order.status,
            workStartDate = order.workStartDate?.makeString() ?: "",
            workEndDate = order.workEndDate?.makeString() ?: "",
            tripEndDate = order.tripEndDate?.makeString() ?: "",
            tripEndLat = order.tripEndLat,
            tripEndLng = order.tripEndLng,
            imageUri = if (order.imageUri != null) Uri.parse(order.imageUri) else null,
            problemDescription = order.problemDescription,
            workDescription = order.workDescription,
            quickReport = order.quickReport,
            services = serviceItems.map { it.toDomainModel() }.toMutableList(),
            usedParts = usedPartsItemDb.map { it.toDomainModel() }.toMutableList(),
            receivedParts = receivedPartsItems.map { it.toDomainModel() }.toMutableList(),
            returnedParts = returnedPartsItems.map { it.toDomainModel() }.toMutableList(),
            engineersItems = engineersItems.map { it.toDomainModel() }.toMutableList(),
            newSpareParts = newSpareParts.map { it.toDomainModel() }.toMutableList(),
            needToCreateGuaranteeOrder = order.needToCreateGuaranteeOrder,
            workHasNoGuarantee = order.workHasNoGuarantee,
            clientRejectedToSign = order.clientRejectedToSign,
            partsAreReceived = order.partsAreReceived,
            isMainUser = order.isMainUser,
            workStarted = order.workStarted,
            isInEngineersList = order.isInEngineersList
        )
    }

    fun toDtoModel(): OrderDto =
        order.run {
            OrderDto(
                guid = orderId,
                number = number,
                date = date.makeString(),
                status = status,
                partner = client,
                workType = workType,
                machinery = machine,
                sn = sn,
                ln = ln,
                en = en,
                driveTime = driveTime,
                driveTimeEnd = driveTimeEnd,
                driveStartDate = driveStartDate?.makeString() ?: "",
                driveEndDate = driveEndDate?.makeString() ?: "",
                workStartDate = workStartDate?.makeString() ?: "",
                workEndDate = workEndDate?.makeString() ?: "",
                tripEndDate = tripEndDate?.makeString() ?: "",
                locationStartLat = locationStartLat,
                locationStartLng = locationStartLng,
                locationLat = locationLat,
                locationLng = locationLng,
                tripEndLat = tripEndLat,
                tripEndLng = tripEndLng,
                problemDescription = problemDescription,
                workDescription = workDescription,
                quickReport = quickReport,
                engineerId = engineerId ?: "",
                engineerName = engineer?.name ?: "",
                needToCreateGuaranteeOrder = needToCreateGuaranteeOrder,
                workHasNoGuarantee = workHasNoGuarantee,
                services = serviceItems.map { it.toDtoModel() },
                usedParts = usedPartsItemDb.map { it.toDtoModel() },
                receivedParts = receivedPartsItems.map { it.toDtoModel() },
                returnedParts = returnedPartsItems.map { it.toDtoModel() },
                newSpareParts = newSpareParts.map { it.toDtoModel() },
                engineerItems = engineersItems.map { it.toDtoModel() },
                clientRejectedToSign = clientRejectedToSign,
                partsAreReceived = order.partsAreReceived,
                isInEngineersList = order.isInEngineersList
            )
        }
}

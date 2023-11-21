package com.example.landtech.data.database.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.landtech.data.remote.dto.ServiceItemDto
import com.example.landtech.domain.models.Engineer
import com.example.landtech.domain.models.ServiceItem
import com.example.landtech.domain.utils.makeString


data class ServiceItemAggregate(
    @Embedded
    val item: ServiceItemDb,

    @Relation(
        parentColumn = "engineer",
        entityColumn = "id"
    )
    val engineer: EngineerDb?,
) {
    fun toDomainModel() = ServiceItem(
        id = item.id,
        date = item.date?.makeString() ?: "",
        time = item.time,
        workType = item.workType,
        measureUnit = item.measureUnit,
        quantity = item.quantity,
        engineer = engineer?.toDomainModel() ?: Engineer(),
        autoGN = item.autoGN,
        isLaborCost = item.isLaborCost
    )

    fun toDtoModel() =
        item.run {
            ServiceItemDto(
                id = id,
                date = date?.makeString() ?: "",
                time = time,
                workType = workType,
                measureUnit = measureUnit,
                quantity = quantity,
                engineer = engineer,
                explotationObject = autoGN
            )
        }
}

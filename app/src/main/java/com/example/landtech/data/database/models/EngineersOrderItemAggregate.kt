package com.example.landtech.data.database.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.landtech.data.remote.dto.EngineerItemDto
import com.example.landtech.domain.models.Engineer
import com.example.landtech.domain.models.EngineersOrderItem

data class EngineersOrderItemAggregate(
    @Embedded
    val item: EngineersOrderItemDb,

    @Relation(
        parentColumn = "engineerId",
        entityColumn = "id"
    )
    val engineer: EngineerDb?,
) {
    fun toDomainModel() = EngineersOrderItem(
        engineer = engineer?.toDomainModel() ?: Engineer(item.engineerId, ""),
        item.orderId
    )

    fun toDtoModel() = EngineerItemDto(item.engineerId, item.orderId)
}


package com.example.landtech.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.landtech.domain.models.Engineer

@Entity(tableName = "engineers")
data class EngineerDb(
    @PrimaryKey var id: String, var name: String
) {
    fun toDomainModel() = Engineer(id = id, name = name)
}
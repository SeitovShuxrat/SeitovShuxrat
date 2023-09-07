package com.example.landtech.data.database.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "orders")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 1,
    var date: String = "",
    var time: String = "",
    var workType: String = "",
    var measureUnit: String = "",
    var quantity: Double = 0.0,
    var engineer: String = "",
    var autoGN: String = "",
    var rowNum: Int = 1
) : Parcelable

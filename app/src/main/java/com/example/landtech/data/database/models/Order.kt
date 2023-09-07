package com.example.landtech.data.database.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 1,
    val number: String = "",
    val date: String = "",
    val client: String = "",
    var engineer: String = "",
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
    val driveTime: Double = 0.0,
    var driveStartDate: String = "",
    var driveEndDate: String = "",
    var typeOrder: String = "",
    var status: String = "",
    val services: MutableList<ServiceItem> = mutableListOf(),
    var workStartDate: String = "",
    var workEndDate: String = "",
    var tripEndDate: String = "",
    var tripEndLat: Double = 0.0,
    var tripEndLng: Double = 0.0,
    var imageUri: Uri? = null
) : Parcelable

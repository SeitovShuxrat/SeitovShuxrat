package com.example.landtech.data.database.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceivedPartsItem(
    val warehouse: String = "",
    val code: String = "",
    val date: String = "",
    val name: String = "",
    val number: String = "",
    val needed: Double = 0.0,
    val inTransit: Double = 0.0,
    val received: Double = 0.0,
    val beingPaid: Double = 0.0,
) : Parcelable

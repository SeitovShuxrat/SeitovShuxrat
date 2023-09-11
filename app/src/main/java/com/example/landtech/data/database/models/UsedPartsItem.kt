package com.example.landtech.data.database.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsedPartsItem(
    val clientUhm: String = "",
    val code: String = "",
    val name: String = "",
    val number: String = "",
    val quantity: Double = 0.00,

    ) : Parcelable

package com.example.landtech.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class NewSparePart(
    var id: String = UUID.randomUUID().toString(),
    var name: String,
    var code: String,
    var quantity: Double,
    var isDiagnose: Boolean
) : Parcelable

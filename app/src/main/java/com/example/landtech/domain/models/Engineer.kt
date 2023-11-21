package com.example.landtech.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Engineer(
    val id: String = "",
    val name: String = ""
) : Parcelable

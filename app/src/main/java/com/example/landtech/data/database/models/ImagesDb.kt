package com.example.landtech.data.database.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "images",
    foreignKeys = [ForeignKey(
        entity = OrderDb::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )],
    indices = [Index("orderId")]
)
data class ImagesDb(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var orderId: String,
    var imageUri: Uri
)

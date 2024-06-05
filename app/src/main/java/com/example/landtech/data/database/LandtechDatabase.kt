package com.example.landtech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.landtech.data.database.models.EngineerDb
import com.example.landtech.data.database.models.EngineersOrderItemDb
import com.example.landtech.data.database.models.ExploitationObjectDb
import com.example.landtech.data.database.models.ImagesDb
import com.example.landtech.data.database.models.NewSparePartDb
import com.example.landtech.data.database.models.OrderDb
import com.example.landtech.data.database.models.ReceivedPartsItemDb
import com.example.landtech.data.database.models.ReturnedPartsItemDb
import com.example.landtech.data.database.models.ServiceItemDb
import com.example.landtech.data.database.models.TransferOrderDb
import com.example.landtech.data.database.models.UsedPartsItemDb

@Database(
    entities = [EngineerDb::class, OrderDb::class, ReceivedPartsItemDb::class,
        ReturnedPartsItemDb::class, ServiceItemDb::class, UsedPartsItemDb::class,
        EngineersOrderItemDb::class, ExploitationObjectDb::class, NewSparePartDb::class,
        TransferOrderDb::class, ImagesDb::class],
    version = 31
)
@TypeConverters(Converters::class)
abstract class LandtechDatabase : RoomDatabase() {
    abstract val dao: LandtechDao
}
package com.example.landtech.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.landtech.data.database.models.EngineerDb
import com.example.landtech.data.database.models.EngineersOrderItemDb
import com.example.landtech.data.database.models.ExploitationObjectAggregate
import com.example.landtech.data.database.models.ExploitationObjectDb
import com.example.landtech.data.database.models.ImagesDb
import com.example.landtech.data.database.models.NewSparePartDb
import com.example.landtech.data.database.models.OrderAggregate
import com.example.landtech.data.database.models.OrderDb
import com.example.landtech.data.database.models.ReceivedPartsItemDb
import com.example.landtech.data.database.models.ReturnedPartsItemDb
import com.example.landtech.data.database.models.ServiceItemDb
import com.example.landtech.data.database.models.TransferOrderDb
import com.example.landtech.data.database.models.UsedPartsItemDb
import kotlinx.coroutines.flow.Flow

@Dao
interface LandtechDao {

    @Query("SELECT (SELECT COUNT(*) FROM orders) == 0")
    fun isEmpty(): Boolean

    @Transaction
    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<OrderAggregate>>

    @Transaction
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY date DESC")
    fun getOrders(status: String): Flow<List<OrderAggregate>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :id")
    fun getOrder(id: String): OrderAggregate?

    @Transaction
    @Query("SELECT * FROM orders WHERE isModified = 1")
    fun getModifiedOrders(): List<OrderAggregate>

    @Transaction
    @Query("SELECT * FROM exploitation_object ORDER BY number")
    fun getAllExploitationObjects(): Flow<List<ExploitationObjectAggregate>>

    @Query("SELECT * FROM engineers ORDER BY name")
    fun getAllEngineers(): Flow<List<EngineerDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEngineers(vararg engineers: EngineerDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExploitationObjects(vararg expObjsDb: ExploitationObjectDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orderDb: OrderDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServiceItems(vararg serviceItemDb: ServiceItemDb?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceivedPartsItems(vararg receivedPartsItemDb: ReceivedPartsItemDb?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReturnedPartsItems(vararg returnedPartsItemDb: ReturnedPartsItemDb?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsedPartsItems(vararg usedPartsItemDb: UsedPartsItemDb?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransferOrders(vararg transferOrders: TransferOrderDb?)

    @Query("SELECT * FROM transfer_order WHERE needToCreate = 1 AND isCreated = 0 AND orderId = :orderId")
    fun getTransferOrdersNeedToCreate(orderId: String): List<TransferOrderDb>

    @Transaction
    fun insertOrderWithItems(
        orderDb: OrderDb,
        servicesItems: List<ServiceItemDb?>,
        receivedItems: List<ReceivedPartsItemDb?>,
        returnedItems: List<ReturnedPartsItemDb?>,
        usedItems: List<UsedPartsItemDb?>,
        engineerItems: List<EngineersOrderItemDb?>
    ) {
        deleteServiceItems(orderDb.orderId)
        deleteReceivedPartItems(orderDb.orderId)
        deleteReturnedPartItems(orderDb.orderId)
        deleteUsedPartItems(orderDb.orderId)
        deleteEngineerItems(orderDb.orderId)
        insertOrder(orderDb)
        insertServiceItems(*servicesItems.toTypedArray())
        insertReceivedPartsItems(*receivedItems.toTypedArray())
        insertReturnedPartsItems(*returnedItems.toTypedArray())
        insertUsedPartsItems(*usedItems.toTypedArray())
        insertEngineerItems(*engineerItems.toTypedArray())
    }

    @Query("SELECT * FROM engineers WHERE id = :engineerId")
    fun getEngineer(engineerId: String): EngineerDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEngineer(engineerDb: EngineerDb)

    @Query("DELETE FROM service_items WHERE orderId = :orderId")
    fun deleteServiceItems(orderId: String)

    @Query("DELETE FROM used_parts_items WHERE orderId = :orderId")
    fun deleteUsedPartItems(orderId: String)

    @Query("DELETE FROM received_parts_items WHERE orderId = :orderId")
    fun deleteReceivedPartItems(orderId: String)

    @Query("DELETE FROM returned_parts_items WHERE orderId = :orderId")
    fun deleteReturnedPartItems(orderId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewSpareParts(vararg newSpareParts: NewSparePartDb)

    @Query("DELETE FROM new_spare_part WHERE orderId = :orderId")
    fun deleteNewSpareParts(orderId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEngineerItems(vararg engineerItems: EngineersOrderItemDb?)

    @Query("DELETE FROM engineers_order_items WHERE orderId = :orderId")
    fun deleteEngineerItems(orderId: String)

    @Query("SELECT * FROM transfer_order WHERE needToCreate = 0 AND isCreated = 0 AND orderId = :orderId")
    fun getTransferOrderListForCreation(orderId: String): List<TransferOrderDb>

    @Query("SELECT * FROM images WHERE orderId = :orderId")
    fun getImages(orderId: String): LiveData<List<ImagesDb>?>

    @Query("SELECT * FROM images WHERE orderId = :orderId")
    fun getImagesSus(orderId: String): List<ImagesDb>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(image: ImagesDb)

    @Delete
    fun removeImage(image: ImagesDb)
}
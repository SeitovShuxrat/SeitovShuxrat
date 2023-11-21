package com.example.landtech.data.repository

import com.example.landtech.data.database.models.EngineerDb
import com.example.landtech.data.database.models.ExploitationObjectAggregate
import com.example.landtech.data.database.models.NewSparePartDb
import com.example.landtech.data.database.models.OrderAggregate
import com.example.landtech.data.database.models.TransferOrderDb
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.data.remote.dto.SparePartDto
import com.example.landtech.domain.models.Order
import com.example.landtech.domain.models.OrderStatus
import com.example.landtech.domain.models.TransferOrder
import kotlinx.coroutines.flow.Flow

interface LandtechRepository {
    val userLoggedIn: Flow<Boolean?>
    fun getAllOrders(status: OrderStatus?): Flow<List<OrderAggregate>>

    fun getAllEngineers(): Flow<List<EngineerDb>>

    fun getAllExploitationObjects(): Flow<List<ExploitationObjectAggregate>>

    suspend fun userLogin(user: String, password: String): Boolean

    suspend fun userLogout()

    suspend fun checkUserLogin(checkFromDataStore: Boolean = false): Boolean

    suspend fun fetchEngineersRemote()

    suspend fun fetchOrdersRemote(): Boolean

    suspend fun fetchExploitationObjectsRemote()

    suspend fun saveOrderToDatabase(order: Order)

    suspend fun sendOrderToServer(orderDb: OrderAggregate): Pair<Boolean, Int>

    suspend fun saveOrderToDatabaseAndSendToServer(it: Order)

    suspend fun saveTransferOrdersToDatabaseAndSendToServer(transferOrders: List<TransferOrder>)

    suspend fun uploadOrdersWithFiles(): Boolean

    suspend fun sendOrderWithFilesToServer(order: OrderAggregate): Boolean

    suspend fun sendOrderFiles(order: OrderAggregate): Boolean

    suspend fun getAllSpareParts(): List<SparePartDto>

    suspend fun insertNewSpareParts(spareParts: List<NewSparePartDb>)

    suspend fun deleteNewSpareParts(orderId: String)

    suspend fun sendNewSpareParts(spareParts: List<NewSparePartDto>): Boolean

    suspend fun getTransferOrderListForCreation(id: String): List<TransferOrder>

}
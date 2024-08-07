package com.example.landtech.data.repository

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.work.WorkManager
import com.example.landtech.data.common.Constants
import com.example.landtech.data.database.LandtechDao
import com.example.landtech.data.database.LandtechDatabase
import com.example.landtech.data.database.models.EngineerDb
import com.example.landtech.data.database.models.ExploitationObjectAggregate
import com.example.landtech.data.database.models.ExploitationObjectDb
import com.example.landtech.data.database.models.ImagesDb
import com.example.landtech.data.database.models.NewSparePartDb
import com.example.landtech.data.database.models.OrderAggregate
import com.example.landtech.data.database.models.OrderDb
import com.example.landtech.data.datastore.LandtechDataStore
import com.example.landtech.data.remote.LandtechServiceAPI
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.data.remote.dto.OrderImages
import com.example.landtech.data.remote.dto.SparePartDto
import com.example.landtech.data.work.FetchOrdersWorker
import com.example.landtech.data.work.UploadOrderWithFilesToServerWorker
import com.example.landtech.domain.models.Order
import com.example.landtech.domain.models.OrderStatus
import com.example.landtech.domain.models.TransferOrder
import com.example.landtech.domain.utils.getFile
import com.example.landtech.domain.utils.showOrderCreatedNotification
import com.example.landtech.domain.utils.toDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Date
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

class LandtechRepositoryImpl(
    private val db: LandtechDatabase,
    private val api: LandtechServiceAPI,
    private val dataStore: LandtechDataStore,
    private val context: Context
) : LandtechRepository {

    private val dao: LandtechDao = db.dao

    override val userLoggedIn: Flow<Boolean?>
        get() = dataStore.isLoggedIn

    override val locationOrderId: Flow<String?>
        get() = dataStore.locationOrderId

    override fun getAllOrders(status: OrderStatus?): Flow<List<OrderAggregate>> {
        return if (status == null)
            dao.getAllOrders()
        else
            dao.getOrders(status.value)
    }

    override fun getAllEngineers(): Flow<List<EngineerDb>> {
        return dao.getAllEngineers()
    }

    override fun getAllExploitationObjects(): Flow<List<ExploitationObjectAggregate>> {
        return dao.getAllExploitationObjects()
    }

    override suspend fun userLogin(user: String, password: String, server: String): Boolean {
        dataStore.saveUserCredentials(user, password, server)
        val success = checkUserLogin()
        dataStore.saveUserLoggedIn(success)
        return success
    }

    override suspend fun checkUserLogin(checkFromDataStore: Boolean): Boolean {
        return try {
            var isLoggedIn = false
            if (checkFromDataStore) {
                isLoggedIn = runBlocking { dataStore.isLoggedIn.first() } ?: false
            }

            if (!isLoggedIn) {
                val response = api.login()
                isLoggedIn = response.isSuccessful

                if (isLoggedIn) {
                    val authBody = response.body()
                    authBody?.token?.let {
                        dataStore.saveUserToken(it)
                    }
                }
            }
            isLoggedIn
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun userLogout() {
        uploadOrdersWithFiles()

        dataStore.saveUserCredentials(user = "", password = "", server = "")
        dataStore.saveUserLoggedIn(false)
        dataStore.saveUserToken("")

        val workManager = WorkManager.getInstance(context)
        workManager.apply {
            cancelUniqueWork(FetchOrdersWorker.WORK_NAME)
            cancelUniqueWork(UploadOrderWithFilesToServerWorker.WORK_NAME)
        }

        db.clearAllTables()
    }

    override suspend fun fetchEngineersRemote() {
        try {
            val engineersDb = api.getEngineers().map {
                it.toDatabaseModel()
            }.toTypedArray()

            dao.insertEngineers(*engineersDb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun fetchExploitationObjectsRemote() {
        try {
            val expObjsDb = api.getExploitationObjects().map {
                val engineerId = if (it.responsiblePersonId != Constants.EMPTY_GUID) {
                    dao.insertEngineer(EngineerDb(it.responsiblePersonId, it.responsiblePerson))
                    it.responsiblePersonId
                } else {
                    null
                }

                ExploitationObjectDb(it.number, engineerId)
            }.toTypedArray()

            dao.insertExploitationObjects(*expObjsDb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun fetchOrdersRemote(): Boolean {
        try {
            val ordersResponse = api.getOrders()

            if (ordersResponse.code() == 401) {
                userLogout()
                return false
            }

            val ordersRemote = ordersResponse.body()

            val dbIsEmpty = dao.isEmpty()

            ordersRemote?.forEach {
                val orderDb = dao.getOrder(it.guid)
                if (orderDb?.order?.status == OrderStatus.ENDED.value || orderDb?.order?.status == OrderStatus.CLOSED.value) {
                    val orderDbNew = orderDb.order.copy(
                        status = it.status,
                        startDate = it.workStartDate.toDate()
                    )

                    dao.insertOrder(orderDbNew)
                    return@forEach
                }

                if (orderDb?.order?.isModified == true) {
                    val receivedItems = it.receivedParts.map { receivedPartsItemDto ->
                        receivedPartsItemDto?.toDatabaseModel(it.guid)
                    }
                    dao.deleteReceivedPartItems(orderDb.order.orderId)
                    dao.insertReceivedPartsItems(*receivedItems.toTypedArray())
                    return@forEach
                }

                val engineerId = if (it.engineerId != Constants.EMPTY_GUID) {
                    dao.insertEngineer(EngineerDb(it.engineerId, it.engineerName))
                    it.engineerId
                } else {
                    null
                }

                val orderDbNew = if (orderDb != null) {
                    orderDb.order.copy(
                        orderId = it.guid,
                        number = it.number,
                        date = it.date.toDate() ?: Date(),
                        client = it.partner,
                        engineerId = engineerId,
                        workType = it.workType,
                        machine = it.machinery,
                        locationLat = orderDb.order.locationLat,
                        locationLng = orderDb.order.locationLng,
                        locationStartLat = orderDb.order.locationStartLat,
                        locationStartLng = orderDb.order.locationStartLng,
                        sn = it.sn,
                        ln = it.ln,
                        en = it.en,
                        driveEndDate = orderDb.order.driveEndDate,
                        driveStartDate = orderDb.order.driveStartDate,
                        driveTime = orderDb.order.driveTime,
                        driveTimeEnd = orderDb.order.driveTimeEnd,
                        typeOrder = it.workType,
                        status = it.status,
                        workStartDate = orderDb.order.workStartDate,
                        workEndDate = orderDb.order.workEndDate,
                        tripEndLat = orderDb.order.tripEndLat,
                        tripEndLng = orderDb.order.tripEndLng,
                        tripEndDate = orderDb.order.tripEndDate,
                        imageUri = orderDb.order.imageUri,
                        problemDescription = it.problemDescription,
                        workDescription = it.workDescription,
                        quickReport = it.quickReport,
                        clientRejectedToSign = it.clientRejectedToSign,
                        partsAreReceived = it.partsAreReceived,
                        isMainUser = it.isMainUser,
                        isInEngineersList = it.isInEngineersList,
                        startDate = it.workStartDate.toDate(),
                    )
                } else {
                    OrderDb(
                        orderId = it.guid,
                        number = it.number,
                        date = it.date.toDate() ?: Date(),
                        startDate = it.workStartDate.toDate(),
                        client = it.partner,
                        engineerId = engineerId,
                        workType = it.workType,
                        machine = it.machinery,
                        driveTime = it.driveTime,
                        driveTimeEnd = it.driveTimeEnd,
                        sn = it.sn,
                        ln = it.ln,
                        en = it.en,
                        typeOrder = it.workType,
                        status = it.status,
                        problemDescription = it.problemDescription,
                        workDescription = it.workDescription,
                        quickReport = it.quickReport,
                        clientRejectedToSign = it.clientRejectedToSign,
                        partsAreReceived = it.partsAreReceived,
                        isMainUser = it.isMainUser,
                        isInEngineersList = it.isInEngineersList
                    )
                }

                val servicesItems = it.services.map { serviceItemDto ->
                    serviceItemDto?.let { servItem ->
                        if (servItem.engineer != Constants.EMPTY_GUID)
                            dao.insertEngineer(EngineerDb(servItem.engineer, servItem.engineerName))
                    }

                    serviceItemDto?.toDatabaseModel(it.guid)
                }

                val receivedItems = it.receivedParts.map { receivedPartsItemDto ->
                    receivedPartsItemDto?.toDatabaseModel(it.guid)
                }

                val returnedItems = it.returnedParts.map { returnedPartsItemDto ->
                    returnedPartsItemDto?.toDatabaseModel(it.guid)
                }

                val usedItems = it.usedParts.map { usedPartsItemDto ->
                    usedPartsItemDto?.toDatabaseModel(it.guid)
                }

                val engineerItems = it.engineerItems.map { engineerItemDto ->
                    engineerItemDto?.toDatabaseModel()
                }

                dao.insertOrderWithItems(
                    orderDbNew,
                    servicesItems,
                    receivedItems,
                    returnedItems,
                    usedItems,
                    engineerItems
                )

                if ((!dbIsEmpty && orderDb == null) || (dbIsEmpty && orderDbNew.status == OrderStatus.NEW.value))
                    showOrderCreatedNotification(orderNumber = orderDbNew.number, context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    override suspend fun saveOrderToDatabase(order: Order) {
        dao.insertOrderWithItems(
            orderDb = order.toDatabaseModel(),
            receivedItems = order.receivedParts.map { it.toDatabaseModel(order.id) },
            servicesItems = order.services.map { it.toDatabaseModel(order.id) },
            returnedItems = order.returnedParts.map { it.toDatabaseModel(order.id) },
            usedItems = order.usedParts.map { it.toDatabaseModel(order.id) },
            engineerItems = order.engineersItems.map { it.toDatabaseModel() }
        )
    }

    override suspend fun sendOrderToServer(orderDb: OrderAggregate): Pair<Boolean, Int> {
        val orderDto = orderDb.toDtoModel()
        val result = api.sendOrder(orderDto)

        return result.isSuccessful to result.code()
    }

    override suspend fun saveOrderToDatabaseAndSendToServer(it: Order) {
        saveOrderToDatabase(it)
        val orderDb = dao.getOrder(it.id)

        try {
            orderDb?.let { sendOrderWithFilesToServer(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun uploadOrdersWithFiles(): Boolean {
        val orderList = dao.getModifiedOrders()

        orderList.forEach {

            try {
                val result = sendOrderWithFilesToServer(it)
                if (!result) return false
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val transferOrdersList = dao.getTransferOrdersNeedToCreate(it.order.orderId)
                val result =
                    api.sendTransferOrders(transferOrdersList.map { item -> item.toDtoModel() })

                if (result.isSuccessful) {
                    dao.insertTransferOrders(*transferOrdersList.map { item -> item.copy(isCreated = true) }
                        .toTypedArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return true
    }

    override suspend fun sendOrderWithFilesToServer(order: OrderAggregate): Boolean {
        val result = sendOrderToServer(order)
        if (result.second == 401) return false

        if (result.first)
            dao.deleteNewSpareParts(order.order.orderId)

        val filesAreUploaded = sendOrderFiles(order)
        if (result.first && filesAreUploaded && order.order.status == OrderStatus.ENDED.value)
            dao.insertOrder(order.order.copy(isModified = false))
        return true
    }

    override suspend fun sendOrderFiles(order: OrderAggregate): Boolean {
        sendImages(order)

        val signImage = if (!order.order.clientRejectedToSign) {
            val signFileArray = context.filesDir.listFiles { _, name ->
                name == "sign_${order.order.orderId}.jpg"
            }

            val signFile = if ((signFileArray?.size ?: 0) > 0) signFileArray?.get(0) else null

            signFile?.let { imgFile ->
                MultipartBody.Part.createFormData(
                    "sign_image",
                    imgFile.name,
                    imgFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
        } else {
            null
        }

        val directoryRecording = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        }

        val recordingsArray = directoryRecording?.listFiles { _, name ->
            name == "land_${order.order.orderId}.mp3"
        }
        val recordingFile = if ((recordingsArray?.size ?: 0) > 0) recordingsArray?.get(0) else null

        val recording = recordingFile?.let { recording ->
            MultipartBody.Part.createFormData(
                "recording",
                recording.name,
                recording.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            )
        }

        if (signImage == null && recording == null)
            return true

        val id = RequestBody.create(MultipartBody.FORM, order.order.orderId)
        val result = api.uploadOrderFiles(id, null, signImage, recording)
        return result.isSuccessful
    }

    override suspend fun getAllSpareParts(
        showOnlyRemainders: Boolean,
        orderId: String?
    ): List<SparePartDto> {
        try {
            return api.getSpareParts(showOnlyRemainders, orderId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return listOf()
    }

    override suspend fun insertNewSpareParts(spareParts: List<NewSparePartDb>) {
        dao.insertNewSpareParts(*spareParts.toTypedArray())
    }

    override suspend fun deleteNewSpareParts(orderId: String) {
        dao.deleteNewSpareParts(orderId)
    }

    override suspend fun sendNewSpareParts(spareParts: List<NewSparePartDto>): Boolean {
        return try {
            val result = api.sendNewSpareParts(spareParts)
            result.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getTransferOrderListForCreation(id: String): List<TransferOrder> {
        try {
            val transferOrdersRemote = api.getTransferOrders()
            dao.insertTransferOrders(*transferOrdersRemote.map { it.toDatabaseModel() }
                .toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dao.getTransferOrderListForCreation(id).map { it.toDomainModel() }
    }

    override suspend fun saveTransferOrdersToDatabaseAndSendToServer(transferOrders: List<TransferOrder>) {
        dao.insertTransferOrders(*transferOrders.map { it.toDatabaseModel() }.toTypedArray())

        try {
            val result = api.sendTransferOrders(transferOrders.map { it.toDtoModel() })

            if (result.isSuccessful)
                dao.insertTransferOrders(*transferOrders.map {
                    it.toDatabaseModel().copy(isCreated = true)
                }.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getImages(orderId: String): LiveData<List<ImagesDb>?> {
        return dao.getImages(orderId)
    }

    override suspend fun addImage(image: ImagesDb) {
        dao.insertImage(image)
    }

    override suspend fun removeImage(image: ImagesDb) {
        dao.removeImage(image)
    }

    override suspend fun sendImages(order: OrderAggregate) {
        val images = dao.getImagesSus(order.order.orderId)
        val imageNamesList = mutableListOf<String>()

        images?.forEach {
            val image = it.imageUri.let { uri ->
                val file = getFile(uri, context, it.toString())
                file?.let { imgFile ->
                    imageNamesList.add(imgFile.name)

                    MultipartBody.Part.createFormData(
                        "image",
                        imgFile.name,
                        imgFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

            }
            if (image != null) {
                val id = RequestBody.create(MultipartBody.FORM, order.order.orderId)
                api.uploadOrderFiles(id, image, null, null)
            }
        }

        api.sendOrderImagesList(OrderImages(order.order.orderId, imageNamesList))
    }
}
package com.example.landtech.presentation.ui.order_details

import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.data.remote.dto.SparePartDto
import com.example.landtech.data.repository.LandtechRepository
import com.example.landtech.domain.models.Engineer
import com.example.landtech.domain.models.EngineersOrderItem
import com.example.landtech.domain.models.ExploitationObject
import com.example.landtech.domain.models.Order
import com.example.landtech.domain.models.OrderStatus
import com.example.landtech.domain.models.ReceivedPartsItem
import com.example.landtech.domain.models.ReturnedPartsItem
import com.example.landtech.domain.models.ScreenStatus
import com.example.landtech.domain.models.ServiceItem
import com.example.landtech.domain.models.TransferOrder
import com.example.landtech.domain.models.UsedPartsItem
import com.example.landtech.domain.utils.combine
import com.example.landtech.domain.utils.makeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(private val repository: LandtechRepository) :
    ViewModel() {

    private val _isMainUser = MutableLiveData(true)
    val isMainUser: LiveData<Boolean> get() = _isMainUser

    private val _isInEngineersList = MutableLiveData(false)
    val isInEngineersList: LiveData<Boolean> get() = _isInEngineersList

    private val _screenStatus = MutableLiveData(ScreenStatus.READY)
    val screenStatus: LiveData<ScreenStatus> get() = _screenStatus

    private val _order = MutableLiveData<Order?>(null)
    val order: LiveData<Order?> get() = _order

    private val _services = MutableLiveData<List<ServiceItem>>(listOf())
    val services: LiveData<List<ServiceItem>> get() = _services

    private var currentServiceItem: ServiceItem? = null

    private val _returnedParts = MutableLiveData<List<ReturnedPartsItem>>(listOf())
    val returnedParts: LiveData<List<ReturnedPartsItem>> get() = _returnedParts

    private val _usedParts = MutableLiveData<List<UsedPartsItem>>(listOf())
    val usedParts: LiveData<List<UsedPartsItem>> get() = _usedParts

    private val addedUsedParts = mutableListOf<UsedPartsItem>()

    private var _usedPartItemAdd = MutableLiveData<UsedPartsItem?>(null)
    val usedPartItemAdd: LiveData<UsedPartsItem?> = _usedPartItemAdd

    private var _worksHaveEnded = MutableLiveData(false)
    val worksHaveEnded: LiveData<Boolean> = _worksHaveEnded

    val locationOrderId = repository.locationOrderId.asLiveData()

    var clientRejectedToSign = false
        private set

    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private var showOnlyRemainders = false

    val quickReportTextWatcher = getTextWatcher {
        isModified = true

        _order.value?.apply {
            isModified = true
            quickReport = it
        }
    }

    val problemDescTextWatcher = getTextWatcher {
        isModified = true

        _order.value?.apply {
            isModified = true
            problemDescription = it
        }
    }

    val workDescTextWatcher = getTextWatcher {
        isModified = true

        _order.value?.apply {
            isModified = true
            workDescription = it
        }
    }

    private val _returnedItemsError = MutableLiveData(false)
//    val returnedItemsError get() = _returnedItemsError
//
//    val returnedQuantityWatcher = getTextWatcher {
//        isModified = true
//
//        _order.value?.apply {
//            isModified = true
//
//        }
//    }

    val addUsedItemQuantityWatcher = getTextWatcher {
        try {
            _usedPartItemAdd.value?.quantity = if (it.isEmpty()) 0.0 else it.toDouble()
        } catch (_: Exception) {
        }
    }

    val newSparePartQuantityWatcher = getTextWatcher {
        try {
            _newAddedSparePart.value = _newAddedSparePart.value?.copy(
                quantity = if (it.isEmpty()) 0.0 else it.toDouble()
            )
        } catch (_: Exception) {
        }
    }

    private var _sparePartsSearchQuery = MutableLiveData("")
    private val sparePartsSearchQuery: LiveData<String> = _sparePartsSearchQuery

    private var _sparePartsList = MutableLiveData<List<SparePartDto>>()
    val sparePartsList: LiveData<List<SparePartDto>> =
        sparePartsSearchQuery.combine(_sparePartsList).switchMap { pair ->
            val list = mutableListOf<SparePartDto>()
            pair.second?.forEach {
                var quantity = it.quantity
                val foundAddedItems = addedUsedParts.filter { up -> up.number == it.number }
                foundAddedItems.forEach { found ->
                    quantity -= found.quantity
                }

                if (quantity > 0)
                    list.add(it.copy(quantity = quantity))
                else
                    list.add(it.copy(quantity = 0.0))
            }
            val query = (pair.first ?: "").lowercase()

            MutableLiveData(
                if (query.isEmpty()) list
                else list.filter {
                    it.name.lowercase().contains(query) || it.number.lowercase().contains(query)
                })
        }

    private var _newAddedSpareParts = MutableLiveData<List<NewSparePartDto>>()
    val newAddedSpareParts: LiveData<List<NewSparePartDto>> = _newAddedSpareParts

    private var _newAddedSparePart = MutableLiveData<NewSparePartDto?>(null)
    val newAddedSparePart: LiveData<NewSparePartDto?> = _newAddedSparePart

    private val _selectedTransferOrders = mutableListOf<TransferOrder>()

    private val _transferOrdersForSelection = MutableLiveData<List<TransferOrder>>(listOf())
    val transferOrdersForSelection = _transferOrdersForSelection

    var isModified = false
        private set

    fun setOrder(order: Order) {
        _order.value = order
        _services.value = order.services
        _usedParts.value = order.usedParts
        _returnedParts.value = order.returnedParts
        _isMainUser.value = order.isMainUser
        _worksHaveEnded.value = order.status == OrderStatus.ENDED.value
        _isInEngineersList.value = order.isInEngineersList

        recalculateReceivedPartsRemainders()
        clientRejectedToSign = order.clientRejectedToSign
    }

    fun setStartLocation(lat: Double, lng: Double) {
        isModified = true
        order.value?.apply {
            isModified = true
            driveStartDate = formatter.format(Date())
            locationStartLat = lat
            locationStartLng = lng
            _order.postValue(this)
        }

        _screenStatus.value = ScreenStatus.READY
    }

    fun setEndLocation(lat: Double, lng: Double, distance: Double) {
        isModified = true
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        order.value?.apply {
            isModified = true
            driveEndDate = formatter.format(Date())
            locationLat = lat
            locationLng = lng

            val typeOfWork = "Расстояние"

            services.removeIf {
                it.workType == typeOfWork
            }

            val startLocation = Location("")
            startLocation.latitude = locationStartLat
            startLocation.longitude = locationStartLng

            services.add(
                ServiceItem(
                    date = driveEndDate.substring(0, 10),
                    time = driveEndDate.substring(11, 19),
                    workType = typeOfWork,
                    engineer = engineer,
                    measureUnit = "км",
                    quantity = distance / 1000.0,
                    isLaborCost = true,
                    dateStart = driveStartDate,
                    dateEnd = driveEndDate,
                    byCurrentUser = true
                )
            )
            _order.postValue(this)
            _services.postValue(services)
        }
        _screenStatus.value = ScreenStatus.READY
    }

    fun setWorkStart() {
        isModified = true
        _order.value?.apply {
            isModified = true
            workStarted = true
            workStartDate = formatter.format(Date())
        }
        setStatus(OrderStatus.IN_WORK)
    }

    fun setWorkEnd() {
        isModified = true

        _order.value?.apply {
            if (!workStarted) return

            isModified = true
            workStarted = false
            val dateEnd = Date()
            val dateStart = formatter.parse(workStartDate)
            val difference = dateEnd.time - (dateStart?.time ?: dateEnd.time)
            val typeOfWork = "Работа"

            workEndDate = formatter.format(dateEnd)

            val unfinishedWorkServices = services.filter { it.workType == typeOfWork && !it.ended }

            if (unfinishedWorkServices.size == 1) {
                unfinishedWorkServices[0].run {
                    engineer = this@apply.engineer
                    date = workEndDate.substring(0, 10)
                    time = workEndDate.substring(11, 19)
                    workType = typeOfWork
                    measureUnit = "ч"
                    quantity = (difference.toDouble() / (1000 * 60 * 60))
                    isLaborCost = true
                    ended = true
                    this.dateStart = workStartDate
                    this.dateEnd = workEndDate
                    byCurrentUser = true
                }
            } else {
                services.add(
                    ServiceItem(
                        date = workEndDate.substring(0, 10),
                        time = workEndDate.substring(11, 19),
                        workType = typeOfWork,
                        engineer = engineer,
                        measureUnit = "ч",
                        quantity = (difference.toDouble() / (1000 * 60 * 60)),
                        isLaborCost = true,
                        ended = true,
                        dateStart = workStartDate,
                        dateEnd = workEndDate,
                        byCurrentUser = true
                    )
                )
            }

            _services.postValue(services)
        }
    }

    fun setAllWorksEnd() {
        isModified = true
        _order.value?.apply {
            isModified = true
        }

        _worksHaveEnded.value = true
        setStatus(OrderStatus.ENDED)
    }

    fun setEndTrip(location: Location) {
        isModified = true
        _order.value?.apply {
            isModified = true
            tripEndDate = formatter.format(Date())
            tripEndLat = location.latitude
            tripEndLng = location.longitude
        }

        _screenStatus.value = ScreenStatus.READY
    }

    fun getEngineersList(): LiveData<List<Engineer>> {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchEngineersRemote()
        }

        return repository.getAllEngineers().asLiveData().map {
            it.map { engineerDb -> engineerDb.toDomainModel() }
        }
    }

    fun getExploitationObjectsList(): LiveData<List<ExploitationObject>> {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchExploitationObjectsRemote()
        }

        return repository.getAllExploitationObjects().asLiveData().map {
            it.map { exploitationObjectAggregate -> exploitationObjectAggregate.toDomainModel() }
        }
    }

    fun startLocationIsSet(): Boolean {
        val orderVal = order.value ?: return false
        return locationOrderId.value == orderVal.id
    }

    fun startWorkIsSet(): Boolean {
        val orderVal = order.value ?: return false
        return orderVal.workStartDate.isNotBlank() && orderVal.workStarted
    }

    fun setAutoDriveTime(value: Double?, isEnd: Boolean) {
        if (value == null) return

        isModified = true
        _order.value?.apply {
            isModified = true
            if (isEnd)
                driveTimeEnd = value
            else driveTime = value


            _order.postValue(this)
        }
    }

    private fun getTextWatcher(afterTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                afterTextChanged(s.toString())
            }
        }
    }

    fun addEngineer(engineer: Engineer) {
        isModified = true
        _order.value?.apply {
            isModified = true
            engineersItems.add(EngineersOrderItem(engineer, id))

            _order.postValue(this)
        }
    }

    private fun setStatus(orderStatus: OrderStatus) {
        _order.value?.apply {
            status = orderStatus.value
        }
    }

    fun saveOrder(): Boolean {
        isModified = false

        order.value?.returnedParts?.forEach {
            if ((it.returned > it.maxQuantity || it.returned == 0.0) && !it.isSaved) {
                _returnedItemsError.value = true
                return false
            }
        }

        _returnedItemsError.value = false

        viewModelScope.launch(Dispatchers.IO) {
            order.value?.let {
                repository.saveOrderToDatabaseAndSendToServer(it)
                repository.saveTransferOrdersToDatabaseAndSendToServer(_selectedTransferOrders)
            }
        }

        return true
    }

    fun reset() {
        isModified = false
    }

    fun screenStatusLoading() {
        _screenStatus.value = ScreenStatus.LOADING
    }

    fun setCreateGuaranteeOrder() {
        isModified = true

        _order.value?.apply {
            isModified = true
            needToCreateGuaranteeOrder = true
        }
    }

    fun setWorkNotGuaranteed() {
        isModified = true

        _order.value?.apply {
            isModified = true
            workHasNoGuarantee = true
        }
    }

    fun setExploitationObject(exploitationObject: ExploitationObject, singular: Boolean) {
        isModified = true

        _order.value?.apply {
            isModified = true

            if (singular) {
                currentServiceItem?.autoGN = exploitationObject.number
//                exploitationObject.engineer?.let { eng ->
//                    currentServiceItem?.engineer = eng
//                } //here changed a single service item. Check if it works
            } else {
                services.forEach {
                    it.autoGN = exploitationObject.number
//                    exploitationObject.engineer?.let { eng ->
//                        it.engineer = eng
//                    }
                }
            }

            _services.postValue(services)
            _order.postValue(this)
        }
    }

    fun clientRejectedToSign(value: Boolean) {
        isModified = true
        clientRejectedToSign = value

        _order.value?.apply {
            isModified = true
            clientRejectedToSign = value
        }
    }

    fun addUsedPartItem() {
        isModified = true

        _order.value?.apply {
            isModified = true
            _usedPartItemAdd.value?.let {
                val foundUsedPartItem = usedParts.find { up ->
                    up.number == it.number
                }

                if (foundUsedPartItem == null) {
                    usedParts.add(it)
                    addedUsedParts.add(it)
                } else {
                    foundUsedPartItem.quantity += it.quantity
                }
            }
            _usedParts.postValue(usedParts)
        }

        recalculateReceivedPartsRemainders()
    }

    fun onNavigateToAddUsedPart(usedPartItem: UsedPartsItem = UsedPartsItem()) {
        _usedPartItemAdd.value = usedPartItem
    }

    fun onNavigateToAddNewSparePart() {
        _newAddedSparePart.value = NewSparePartDto(
            UUID.randomUUID().toString(), _order.value?.id ?: "", "", "", 0.0, false
        )
    }

    fun onNavigateToAddNewSparePartsList() {
        _newAddedSpareParts.value = listOf()
    }

    fun changeClientUhm(value: String) {
        _usedPartItemAdd.value?.clientUhm = value
    }

    fun clearUsedPartAdd() {
        _usedPartItemAdd.value = null
    }

    fun usedPartAddFieldsAreSet(): Boolean {
        return usedPartItemAdd.value?.run {
            if (clientUhm == "UHM LT склад") {
                code.isNotBlank() && clientUhm.isNotBlank() && quantity > 0
            } else {
                clientUhm.isNotBlank() && quantity > 0
            }
        } ?: false
    }

    fun setSparePartUsedPartAdd(sparePart: SparePartDto) {
        _usedPartItemAdd.value?.apply {
            code = sparePart.code
            name = sparePart.name
            number = sparePart.number
            quantityWarehouse = sparePart.quantity

            _usedPartItemAdd.postValue(this)
        }
    }

    fun fetchSparePartList(showOnlyRemainders: Boolean, orderId: String?) {
        this.showOnlyRemainders = showOnlyRemainders
        viewModelScope.launch(Dispatchers.IO) {
            _sparePartsList.postValue(repository.getAllSpareParts(showOnlyRemainders, orderId))
        }
    }

    fun setNewSparePartItem(sparePartDto: SparePartDto) {
        _newAddedSparePart.value = _newAddedSparePart.value?.copy(
            code = sparePartDto.number, name = sparePartDto.name
        )
    }

    fun clearNewSparePart() {
        _newAddedSparePart.value = null
    }

    fun addNewSparePartToList() {
        val newList = mutableListOf<NewSparePartDto>()
        _newAddedSpareParts.value?.forEach {
            newList.add(it)
        }

        _newAddedSparePart.value?.let {
            newList.add(it)
        }

        _newAddedSpareParts.value = newList

        clearNewSparePart()
    }

    fun saveNewSparePartsList(isDiagnose: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _newAddedSpareParts.value?.let {
                val isSuccessful = repository.sendNewSpareParts(it.map { nsp ->
                    nsp.isDiagnose = isDiagnose
                    nsp
                })

                if (!isSuccessful) repository.insertNewSpareParts(it.map { newSparePartDto ->
                    newSparePartDto.isDiagnose = isDiagnose
                    newSparePartDto.toDatabaseModel()
                })
            }
        }
    }

    fun setModified(b: Boolean) {
        isModified = b
        _order.value?.isModified = b
    }

    fun addReturnedPart(item: ReceivedPartsItem) {
        isModified = true

        _order.value?.apply {
            isModified = true

            returnedParts.add(
                ReturnedPartsItem(
                    warehouse = item.warehouse,
                    code = item.code,
                    date = Date().makeString(),
                    name = item.name,
                    number = item.number,
                    returned = item.received,
                    maxQuantity = item.received
                )
            )
            recalculateReceivedPartsRemainders()
            _returnedParts.postValue(returnedParts)
        }
    }

    fun deleteNewAddedSparePart(newSparePartDto: NewSparePartDto) {
        val newList = _newAddedSpareParts.value?.toMutableList()
        newList?.remove(newSparePartDto)

        newList?.let {
            _newAddedSpareParts.value = it
        }
    }

    fun deleteUsedSparePart(item: UsedPartsItem) {
        val newList = _usedParts.value?.toMutableList()
        newList?.remove(item)
        _order.value?.usedParts?.remove(item)
        addedUsedParts.remove(item)

        newList?.let {
            _usedParts.value = it

        }
    }

    fun deleteReturnedSparePart(item: ReturnedPartsItem) {
        isModified = true

        _order.value?.apply {
            isModified = true
            returnedParts.remove(item)

            recalculateReceivedPartsRemainders()
            _returnedParts.postValue(returnedParts)
        }
    }

    fun setReturnedItemQuantity(item: ReturnedPartsItem, quantity: Double) {
        isModified = true

        _order.value?.apply {
            isModified = true

            item.returned = quantity
        }

        recalculateReceivedPartsRemainders()
    }

    fun getTransferOrderListForSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            _order.value?.let {
                val transferOrderList =
                    repository.getTransferOrderListForCreation(it.id).toMutableList()

                transferOrderList.removeIf {
                    _selectedTransferOrders.any { to -> to.id == it.id }
                }

                _transferOrdersForSelection.postValue(transferOrderList)
            }
        }
    }

    fun addTransferOrder(transferOrder: TransferOrder) {
        _selectedTransferOrders.add(transferOrder.copy(needToCreate = true))
    }

    fun setUsedPartAddNumber(number: String) {
        if (usedPartItemAdd.value?.clientUhm == "Клиент")
            _usedPartItemAdd.value = _usedPartItemAdd.value?.copy(number = number)
    }

    fun setSparePartsListSearch(query: String) {
        _sparePartsSearchQuery.value = query
    }

    fun setCurrentServiceItem(serviceItem: ServiceItem) {
        currentServiceItem = serviceItem
    }

    private fun recalculateReceivedPartsRemainders() {
        _order.value?.receivedPartsRemainders?.apply {
            clear()
            // map.key.first - Warehouse
            // map.key.second - Number
            // map.value.first - Name
            // map.value.second - Quantity received
            // map.value.third - Code
            val map = mutableMapOf<Pair<String, String>, Triple<String, Double, String>>()

            _order.value?.receivedParts?.forEach {
                val key = it.warehouse to it.number
                map[key] = Triple(it.name, (it.received + (map[key]?.second ?: 0.0)), it.code)
            }

            map.forEach { (key, value) ->
                val foundReturnedItems = _returnedParts.value?.filter { returnedItem ->
                    returnedItem.number == key.second && returnedItem.warehouse == key.first
                }

                foundReturnedItems?.forEach { returnedItem ->
                    map[key] =
                        Triple(
                            value.first,
                            (map[key]?.second?.minus(returnedItem.returned) ?: 0.0),
                            value.third
                        )
                }

                val foundUsedItems = _usedParts.value?.filter { usedItem ->
                    usedItem.number == key.second
                }

                foundUsedItems?.forEach { usedItem ->
                    map[key] =
                        Triple(
                            value.first,
                            (map[key]?.second?.minus(usedItem.quantity) ?: 0.0),
                            value.third
                        )
                }
            }

            map.forEach { mapItem ->
                if (mapItem.value.second > 0) {
                    add(
                        ReceivedPartsItem(
                            warehouse = mapItem.key.first,
                            number = mapItem.key.second,
                            name = mapItem.value.first,
                            received = mapItem.value.second,
                            code = mapItem.value.third
                        )
                    )
                }
            }
        }
    }

    fun usedPartQuantityCheck(): Boolean {
        usedPartItemAdd.value?.let {
            return it.quantity <= it.quantityWarehouse
        }

        return false
    }

    fun clearAddedUsedParts() {
        addedUsedParts.clear()
    }

    fun clearSparePartsList() {
        _sparePartsList.value = emptyList()
    }
}
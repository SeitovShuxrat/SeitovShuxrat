package com.example.landtech.presentation.ui.order_details

import android.location.Location
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
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

    private val _screenStatus = MutableLiveData(ScreenStatus.READY)
    val screenStatus = _screenStatus

    private val _order = MutableLiveData<Order?>(null)
    val order: LiveData<Order?> get() = _order

    private val _services = MutableLiveData<List<ServiceItem>>(listOf())
    val services: LiveData<List<ServiceItem>> get() = _services


    private val _returnedParts = MutableLiveData<List<ReturnedPartsItem>>(listOf())
    val returnedParts: LiveData<List<ReturnedPartsItem>> get() = _returnedParts

    private val _usedParts = MutableLiveData<List<UsedPartsItem>>(listOf())
    val usedParts: LiveData<List<UsedPartsItem>> get() = _usedParts

    private var _usedPartItemAdd = MutableLiveData<UsedPartsItem?>(null)
    val usedPartItemAdd: LiveData<UsedPartsItem?> = _usedPartItemAdd

    var clientRejectedToSign = false
        private set

    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

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

    val returnedQuantityWatcher = getTextWatcher {
        isModified = true

        _order.value?.apply {
            isModified = true

        }
    }

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

    private var _sparePartsList = MutableLiveData<List<SparePartDto>>()
    val sparePartsList: LiveData<List<SparePartDto>> = _sparePartsList

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

        clientRejectedToSign = order.clientRejectedToSign
    }

    fun setStartLocation(location: Location) {
        isModified = true
        order.value?.apply {
            isModified = true
            driveStartDate = formatter.format(Date())
            locationStartLat = location.latitude
            locationStartLng = location.longitude
            _order.postValue(this)
        }

        _screenStatus.value = ScreenStatus.READY
    }

    fun setEndLocation(location: Location) {
        isModified = true
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        order.value?.apply {
            isModified = true
            driveEndDate = formatter.format(Date())
            locationLat = location.latitude
            locationLng = location.longitude

            val typeOfWork = "Расстояние"

            services.removeIf {
                it.workType == typeOfWork
            }

            val startLocation = Location("")
            startLocation.latitude = locationStartLat
            startLocation.longitude = locationStartLng

            val distance = location.distanceTo(startLocation)

            services.add(
                ServiceItem(
                    date = driveEndDate.substring(0, 10),
                    time = driveEndDate.substring(11, 19),
                    workType = typeOfWork,
                    engineer = engineer,
                    measureUnit = "км",
                    quantity = distance / 1000.0,
                    isLaborCost = true
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
            workStartDate = formatter.format(Date())
        }
        setStatus(OrderStatus.IN_RESERVE)
    }

    fun setWorkEnd() {
        isModified = true
        _order.value?.apply {
            isModified = true
            val dateEnd = Date()
            val dateStart = formatter.parse(workStartDate)
            val difference = dateEnd.time - (dateStart?.time ?: dateEnd.time)
            val typeOfWork = "Работа"

            workEndDate = formatter.format(dateEnd)

            services.removeIf {
                it.workType == typeOfWork
            }

            services.add(
                ServiceItem(
                    date = workEndDate.substring(0, 10),
                    time = workEndDate.substring(11, 19),
                    workType = typeOfWork,
                    engineer = engineer,
                    measureUnit = "ч",
                    quantity = (difference.toDouble() / (1000 * 60 * 60)),
                    isLaborCost = true
                )
            )
            setStatus(OrderStatus.CLOSED)
            _services.postValue(services)
        }
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
        return !(orderVal.locationStartLat == 0.0 && orderVal.locationStartLng == 0.0)
    }

    fun startWorkIsSet(): Boolean {
        val orderVal = order.value ?: return false
        return orderVal.workStartDate.isNotBlank()
    }

    fun setImage(uri: Uri) {
        isModified = true
        _order.value?.apply {
            isModified = true
            imageUri = uri
        }
    }

    fun setAutoDriveTime(value: Double?) {
        if (value == null) return

        isModified = true
        _order.value?.apply {
            isModified = true
            driveTime = value
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
            engineersItems.add(EngineersOrderItem(engineer.id, id))
        }
    }

    private fun setStatus(orderStatus: OrderStatus) {
        _order.value?.apply {
            status = orderStatus.value
        }
    }

    fun saveOrder() {
        isModified = false

        viewModelScope.launch(Dispatchers.IO) {
            order.value?.let {
                repository.saveOrderToDatabaseAndSendToServer(it)
                repository.saveTransferOrdersToDatabaseAndSendToServer(_selectedTransferOrders)
            }
        }
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

    fun setExploitationObject(exploitationObject: ExploitationObject) {
        isModified = true

        _order.value?.apply {
            isModified = true
            services.forEach {
                it.autoGN = exploitationObject.number
                exploitationObject.engineer?.let { eng ->
                    it.engineer = eng
                }
            }

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
            _usedPartItemAdd.value?.let { usedParts.add(it) }
            _usedParts.postValue(usedParts)
        }
    }

    fun onNavigateToAddUsedPart(usedPartItem: UsedPartsItem = UsedPartsItem()) {
        _usedPartItemAdd.value = usedPartItem
    }

    fun onNavigateToAddNewSparePart() {
        _newAddedSparePart.value =
            NewSparePartDto(
                UUID.randomUUID().toString(),
                _order.value?.id ?: "",
                "",
                "",
                0.0,
                false
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

            _usedPartItemAdd.postValue(this)
        }
    }

    fun fetchSparePartList() {
        viewModelScope.launch(Dispatchers.IO) {
            _sparePartsList.postValue(repository.getAllSpareParts())
        }
    }

    fun setNewSparePartItem(sparePartDto: SparePartDto) {
        _newAddedSparePart.value = _newAddedSparePart.value?.copy(
            code = sparePartDto.code,
            name = sparePartDto.name
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
                val isSuccessful =
                    repository.sendNewSpareParts(it.map { nsp ->
                        nsp.isDiagnose = isDiagnose
                        nsp
                    })

                if (!isSuccessful)
                    repository.insertNewSpareParts(it.map { newSparePartDto ->
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
                    returned = item.received
                )
            )

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

    fun deleteReturnedSparePart(item: ReturnedPartsItem) {
        isModified = true

        _order.value?.apply {
            isModified = true
            returnedParts.remove(item)
            _returnedParts.postValue(returnedParts)
        }
    }

    fun setReturnedItemQuantity(item: ReturnedPartsItem, quantity: Double) {
        isModified = true

        _order.value?.apply {
            isModified = true

            item.returned = quantity
        }
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
}
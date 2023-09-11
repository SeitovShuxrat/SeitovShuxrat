package com.example.landtech.presentation.ui.order_details

import android.location.Location
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landtech.data.database.models.Order
import com.example.landtech.data.database.models.ServiceItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.hours

class OrderDetailsViewModel : ViewModel() {
    private val _order = MutableLiveData<Order?>(null)
    val order: LiveData<Order?> get() = _order

    private val _services = MutableLiveData<List<ServiceItem>>(listOf())
    val services: LiveData<List<ServiceItem>> get() = _services
    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    val quickReportTextWatcher = getTextWatcher {
        _order.value?.quickReport = it
    }

    val problemDescTextWatcher = getTextWatcher {
        _order.value?.problemDescription = it
    }

    val workDescTextWatcher = getTextWatcher {
        _order.value?.workDescription = it
    }

    fun setOrder(order: Order) {
        _order.value = order
        _services.value = order.services
    }

    fun setStartLocation(location: Location) {
        order.value?.apply {
            driveStartDate = formatter.format(Date())
            locationStartLat = location.latitude
            locationStartLng = location.longitude
            _order.postValue(this)
        }
    }

    fun setEndLocation(location: Location) {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        order.value?.apply {
            driveEndDate = formatter.format(Date())
            locationLat = location.latitude
            locationLng = location.longitude

            services.removeIf {
                it.measureUnit == "км" && it.workType == workType
            }

            val startLocation = Location("")
            startLocation.latitude = locationStartLat
            startLocation.longitude = locationStartLng

            val distance = location.distanceTo(startLocation)

            services.add(
                ServiceItem(
                    date = driveEndDate.substring(0, 10),
                    time = driveEndDate.substring(11, 19),
                    workType = workType,
                    engineer = engineer,
                    measureUnit = "км",
                    quantity = distance / 1000.0
                )
            )
            _order.postValue(this)
            _services.postValue(services)
        }
    }

    fun setWorkStart() {
        _order.value?.workStartDate = formatter.format(Date())
    }

    fun setWorkEnd() {
        _order.value?.apply {
            val dateEnd = Date()
            val dateStart = formatter.parse(workStartDate)
            val difference = dateEnd.time - (dateStart?.time ?: dateEnd.time)

            workEndDate = formatter.format(dateEnd)

            services.removeIf {
                it.measureUnit == "ч" && it.workType == workType
            }

            services.add(
                ServiceItem(
                    date = workEndDate.substring(0, 10),
                    time = workEndDate.substring(11, 19),
                    workType = workType,
                    engineer = engineer,
                    measureUnit = "ч",
                    quantity = (difference.toDouble() / (1000 * 60 * 60))
                )
            )

            _services.postValue(services)
        }
    }

    fun setEndTrip(location: Location) {
        _order.value?.apply {
            tripEndDate = formatter.format(Date())
            tripEndLat = location.latitude
            tripEndLng = location.longitude
        }
    }

    fun startLocationIsSet(): Boolean {
        val orderVal = order.value
        return !(orderVal?.locationStartLat == 0.0 && orderVal.locationStartLng == 0.0)
    }

    fun setImage(imageUri: Uri) {
        _order.value?.imageUri = imageUri
    }

    fun setEngineer(newEngineer: String) {
        _order.value?.apply {
            engineer = newEngineer
            _order.postValue(this)
        }
    }

    fun setAutoDriveTime(value: Double?) {
        if (value == null) return

        _order.value?.apply {
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
}
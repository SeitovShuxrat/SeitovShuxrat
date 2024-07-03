package com.example.landtech.presentation.ui.orders

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.landtech.data.repository.LandtechRepository
import com.example.landtech.data.work.FetchOrdersWorker
import com.example.landtech.data.work.UploadOrderWithFilesToServerWorker
import com.example.landtech.domain.models.Order
import com.example.landtech.domain.models.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: LandtechRepository,
    @ApplicationContext context: Context
) :
    ViewModel() {

    val userLoggedIn = repository.userLoggedIn.asLiveData()

    private val ordersFilterValue = MutableLiveData<OrderStatus?>(null)
    val orders: LiveData<List<Order>> = ordersFilterValue.switchMap { status ->
        repository.getAllOrders(status).asLiveData().map {
            it.map { orderAggregate ->
                orderAggregate.toDomainModel()
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchOrdersRemote()

            val workRequest = OneTimeWorkRequestBuilder<FetchOrdersWorker>()
                .addTag(FetchOrdersWorker.TAG)
                .setInitialDelay(2, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    FetchOrdersWorker.WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )

            val workRequestUploadOrders =
                OneTimeWorkRequestBuilder<UploadOrderWithFilesToServerWorker>()
                    .setInitialDelay(2, TimeUnit.MINUTES)
                    .addTag(UploadOrderWithFilesToServerWorker.TAG)
                    .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    UploadOrderWithFilesToServerWorker.WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    workRequestUploadOrders
                )
        }
    }

    fun setStatusFilter(status: OrderStatus?) {
        ordersFilterValue.value = status
    }

    fun logout(doAfter: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userLogout()
            withContext(Dispatchers.Main) {
                doAfter()
            }
        }
    }
}
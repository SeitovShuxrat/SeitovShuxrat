package com.example.landtech.domain.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.Order
import com.example.landtech.data.database.models.ServiceItem
import com.example.landtech.presentation.ui.order_details.tab_fragments.work.ServicesListAdapter
import com.example.landtech.presentation.ui.orders.OrdersListAdapter

@BindingAdapter("ordersListData")
fun bindOrdersListData(recyclerView: RecyclerView, data: List<Order>?) {
    val adapter = recyclerView.adapter as OrdersListAdapter
    adapter.submitList(data)
}

@BindingAdapter("servicesListData")
fun bindServicesListData(recyclerView: RecyclerView, data: List<ServiceItem>?) {
    data?.forEachIndexed { index, serviceItem ->
        serviceItem.rowNum = index + 1
    }

    val adapter = recyclerView.adapter as ServicesListAdapter
    adapter.submitList(data)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("setLocationCoordinates")
fun setCoordinates(tw: TextView, order: Order) {
    tw.text = "(${order.locationLat}, ${order.locationLng})"
}
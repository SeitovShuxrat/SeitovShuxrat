package com.example.landtech.presentation.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.Order
import com.example.landtech.databinding.OrderItemBinding

class OrdersListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Order, OrdersListAdapter.OrderVH>(
        object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Order, newItem: Order) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(inflater, parent, false)

        return OrderVH(binding)
    }

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    class OrderVH(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Order, onClickListener: OnClickListener) {
            binding.order = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
        }
    }

    class OnClickListener(private var onClickListener: (Order) -> Unit) {
        fun onClick(category: Order) = onClickListener(category)
    }
}
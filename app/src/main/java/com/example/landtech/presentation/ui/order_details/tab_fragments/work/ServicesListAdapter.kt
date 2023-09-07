package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.ServiceItem
import com.example.landtech.databinding.ServiceItemBinding

class ServicesListAdapter : ListAdapter<ServiceItem, ServicesListAdapter.VH>(
    object : DiffUtil.ItemCallback<ServiceItem>() {
        override fun areItemsTheSame(oldItem: ServiceItem, newItem: ServiceItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ServiceItem, newItem: ServiceItem) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ServiceItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceItem) {
            binding.serviceItem = item
        }
    }
}
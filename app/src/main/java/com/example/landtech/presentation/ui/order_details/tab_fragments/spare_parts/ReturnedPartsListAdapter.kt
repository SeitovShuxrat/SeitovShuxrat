package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.ReturnedPartsItem
import com.example.landtech.databinding.ReturnedPartsItemBinding


class ReturnedPartsListAdapter : ListAdapter<ReturnedPartsItem, ReturnedPartsListAdapter.VH>(

    object : DiffUtil.ItemCallback<ReturnedPartsItem>() {
        override fun areItemsTheSame(oldItem: ReturnedPartsItem, newItem: ReturnedPartsItem) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: ReturnedPartsItem, newItem: ReturnedPartsItem) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReturnedPartsItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ReturnedPartsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReturnedPartsItem) {
            binding.item = item
        }
    }
}
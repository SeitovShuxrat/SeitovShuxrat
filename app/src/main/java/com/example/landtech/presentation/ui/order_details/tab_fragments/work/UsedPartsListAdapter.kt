package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.UsedPartsItem
import com.example.landtech.databinding.UsedSparePartsItemBinding


class UsedPartsListAdapter : ListAdapter<UsedPartsItem, UsedPartsListAdapter.VH>(

    object : DiffUtil.ItemCallback<UsedPartsItem>() {
        override fun areItemsTheSame(oldItem: UsedPartsItem, newItem: UsedPartsItem) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: UsedPartsItem, newItem: UsedPartsItem) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UsedSparePartsItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: UsedSparePartsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UsedPartsItem) {
            binding.item = item
        }
    }
}
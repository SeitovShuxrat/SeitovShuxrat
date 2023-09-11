package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.database.models.ReceivedPartsItem
import com.example.landtech.databinding.ReceivedPartsItemBinding

class ReceivedPartsListAdapter : ListAdapter<ReceivedPartsItem, ReceivedPartsListAdapter.VH>(

    object : DiffUtil.ItemCallback<ReceivedPartsItem>() {
        override fun areItemsTheSame(oldItem: ReceivedPartsItem, newItem: ReceivedPartsItem) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: ReceivedPartsItem, newItem: ReceivedPartsItem) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReceivedPartsItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ReceivedPartsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReceivedPartsItem) {
            binding.item = item
        }
    }
}
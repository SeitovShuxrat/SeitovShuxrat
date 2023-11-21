package com.example.landtech.presentation.ui.select_received_part

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.databinding.SelectReceivedSparePartItemBinding
import com.example.landtech.domain.models.ReceivedPartsItem

class SelectReceivedPartListAdapter(private val onClick: (ReceivedPartsItem) -> Unit) :
    ListAdapter<ReceivedPartsItem, SelectReceivedPartListAdapter.VH>(object :
        DiffUtil.ItemCallback<ReceivedPartsItem>() {
        override fun areItemsTheSame(
            oldItem: ReceivedPartsItem,
            newItem: ReceivedPartsItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ReceivedPartsItem,
            newItem: ReceivedPartsItem
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SelectReceivedSparePartItemBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class VH(
        private val binding: SelectReceivedSparePartItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReceivedPartsItem, onClick: (ReceivedPartsItem) -> Unit) {
            binding.item = item
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}
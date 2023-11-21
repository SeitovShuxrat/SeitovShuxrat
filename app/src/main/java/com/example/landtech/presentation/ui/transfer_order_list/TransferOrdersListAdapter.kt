package com.example.landtech.presentation.ui.transfer_order_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.databinding.TransferOrdersItemBinding
import com.example.landtech.domain.models.TransferOrder

class TransferOrdersListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<TransferOrder, TransferOrdersListAdapter.VH>(
        object : DiffUtil.ItemCallback<TransferOrder>() {
            override fun areItemsTheSame(oldItem: TransferOrder, newItem: TransferOrder) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TransferOrder, newItem: TransferOrder) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TransferOrdersItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    class VH(private val binding: TransferOrdersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransferOrder, onClickListener: OnClickListener) {
            binding.transferOrder = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
        }
    }

    class OnClickListener(private var onClickListener: (TransferOrder) -> Unit) {
        fun onClick(item: TransferOrder) = onClickListener(item)
    }
}
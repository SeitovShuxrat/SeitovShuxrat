package com.example.landtech.presentation.ui.select_spare_part

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.data.remote.dto.SparePartDto
import com.example.landtech.databinding.SparePartSelectItemBinding

class SelectSparePartAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<SparePartDto, SelectSparePartAdapter.VH>(
        object : DiffUtil.ItemCallback<SparePartDto>() {
            override fun areItemsTheSame(oldItem: SparePartDto, newItem: SparePartDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SparePartDto, newItem: SparePartDto) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SparePartSelectItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    class VH(private val binding: SparePartSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SparePartDto, onClickListener: OnClickListener) {
            binding.sparePart = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
        }
    }

    class OnClickListener(private var onClickListener: (SparePartDto) -> Unit) {
        fun onClick(item: SparePartDto) = onClickListener(item)
    }
}
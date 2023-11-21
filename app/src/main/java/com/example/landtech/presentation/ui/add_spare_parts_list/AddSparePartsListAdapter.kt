package com.example.landtech.presentation.ui.add_spare_parts_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.databinding.AddSparePartItemBinding


class AddSparePartsListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<NewSparePartDto, AddSparePartsListAdapter.VH>(
        object : DiffUtil.ItemCallback<NewSparePartDto>() {
            override fun areItemsTheSame(oldItem: NewSparePartDto, newItem: NewSparePartDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NewSparePartDto, newItem: NewSparePartDto) =
                oldItem == newItem
        }
    ) {

    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AddSparePartItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClickListener, viewBinderHelper)
    }

    class VH(private val binding: AddSparePartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: NewSparePartDto,
            onClickListener: OnClickListener,
            viewBinderHelper: ViewBinderHelper
        ) {
            binding.sparePart = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
            binding.deleteBtn.setOnClickListener {
                onClickListener.onDeleteClick(item)
            }

            viewBinderHelper.bind(binding.swipeRevealLayout, item.id)
        }
    }

    class OnClickListener(
        private var onClickListener: (NewSparePartDto) -> Unit,
        private var onDeleteClickListener: (NewSparePartDto) -> Unit
    ) {
        fun onClick(item: NewSparePartDto) = onClickListener(item)

        fun onDeleteClick(item: NewSparePartDto) = onDeleteClickListener(item)
    }
}
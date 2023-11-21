package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.landtech.domain.models.ReturnedPartsItem
import com.example.landtech.databinding.ReturnedPartsItemBinding


class ReturnedPartsListAdapter(
    private val onReturnedQuantityChanged: (ReturnedPartsItem, Double) -> Unit,
    private val onDeleteClickListener: (ReturnedPartsItem) -> Unit
) :
    ListAdapter<ReturnedPartsItem, ReturnedPartsListAdapter.VH>(

        object : DiffUtil.ItemCallback<ReturnedPartsItem>() {
            override fun areItemsTheSame(oldItem: ReturnedPartsItem, newItem: ReturnedPartsItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ReturnedPartsItem,
                newItem: ReturnedPartsItem
            ) =
                oldItem == newItem
        }
    ) {

    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReturnedPartsItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(
            getItem(position),
            viewBinderHelper,
            onDeleteClickListener,
            onReturnedQuantityChanged
        )
    }

    class VH(private val binding: ReturnedPartsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ReturnedPartsItem,
            viewBinderHelper: ViewBinderHelper,
            onDeleteClickListener: (ReturnedPartsItem) -> Unit,
            onQuantityChanged: (ReturnedPartsItem, Double) -> Unit
        ) {
            binding.item = item
            binding.deleteBtn.apply {
                isEnabled = item.isDeletable
                setOnClickListener {
                    onDeleteClickListener(item)
                }
            }

            binding.returnedTV.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    onQuantityChanged(item, s.toString().toDouble())
                }
            })
            viewBinderHelper.bind(binding.swipeRevealLayout, item.id)
            if (!item.isDeletable)
                viewBinderHelper.lockSwipe(item.id)

            binding.returnedTV.isEnabled = item.isDeletable
        }
    }
}
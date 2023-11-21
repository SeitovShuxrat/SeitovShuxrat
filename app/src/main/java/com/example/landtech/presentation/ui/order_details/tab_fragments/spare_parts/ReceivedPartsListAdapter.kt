package com.example.landtech.presentation.ui.order_details.tab_fragments.spare_parts

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.domain.models.ReceivedPartsItem
import com.example.landtech.databinding.ReceivedPartsItemBinding

class ReceivedPartsListAdapter(private val onReceivedChanged: () -> Unit) :
    ListAdapter<ReceivedPartsItem, ReceivedPartsListAdapter.VH>(

        object : DiffUtil.ItemCallback<ReceivedPartsItem>() {
            override fun areItemsTheSame(oldItem: ReceivedPartsItem, newItem: ReceivedPartsItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ReceivedPartsItem,
                newItem: ReceivedPartsItem
            ) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ReceivedPartsItemBinding.inflate(inflater, parent, false)

        return VH(binding, onReceivedChanged)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ReceivedPartsItemBinding,
        private val onReceivedChanged: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReceivedPartsItem) {
            binding.item = item

            binding.receivedTV.isEnabled = item.inTransit > 0

            if (item.received > 0)
                binding.receivedTV.setText(item.received.toString())

            binding.receivedTV.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    try {
                        item.received = if (s?.isEmpty() == true) 0.0 else s.toString().toDouble()
                        onReceivedChanged()
                    } catch (_: Exception) {
                    }
                }
            })
        }
    }
}
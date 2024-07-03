package com.example.landtech.presentation.ui.order_details.tab_fragments.work

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.domain.models.ServiceItem
import com.example.landtech.databinding.ServiceItemBinding
import java.util.Locale

class ServicesListAdapter(
    private var worksHaveEnded: Boolean = false,
    private val onQuantityChanged: () -> Unit,
    private val onAutoGnClicked: (ServiceItem) -> Unit
) :
    ListAdapter<ServiceItem, ServicesListAdapter.VH>(
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
        holder.bind(getItem(position), worksHaveEnded, onQuantityChanged, onAutoGnClicked)
    }

    class VH(private val binding: ServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun disableFields() {
            binding.quantityTV.isEnabled = false
            binding.autoGnTV.isEnabled = false
        }

        fun bind(
            item: ServiceItem,
            worksHaveEnded: Boolean,
            onQuantityChanged: () -> Unit,
            onAutoGnClicked: (ServiceItem) -> Unit
        ) {
            binding.serviceItem = item

            if (worksHaveEnded) disableFields()

            binding.quantityTV.setText(String.format(Locale.ENGLISH, "%.3f", item.quantity))
            binding.quantityTV.addTextChangedListener {
                item.quantity = if (it?.isEmpty() == false) it.toString().toDouble() else 0.0
                onQuantityChanged()
            }

            binding.autoGnTV.setOnClickListener {
                onAutoGnClicked(item)
            }
        }
    }
}
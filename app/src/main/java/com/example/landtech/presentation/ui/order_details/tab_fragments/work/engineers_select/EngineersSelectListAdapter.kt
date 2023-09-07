package com.example.landtech.presentation.ui.order_details.tab_fragments.work.engineers_select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.databinding.EngineersSelectItemBinding

class EngineersSelectListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<String, EngineersSelectListAdapter.VH>(
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EngineersSelectItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    class VH(private val binding: EngineersSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, onClickListener: OnClickListener) {
            binding.engineer = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
        }
    }

    class OnClickListener(private var onClickListener: (String) -> Unit) {
        fun onClick(item: String) = onClickListener(item)
    }
}
package com.example.landtech.presentation.ui.engineers_select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.landtech.databinding.EngineersSelectItemBinding
import com.example.landtech.domain.models.Engineer

class EngineersSelectListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Engineer, EngineersSelectListAdapter.VH>(
        object : DiffUtil.ItemCallback<Engineer>() {
            override fun areItemsTheSame(oldItem: Engineer, newItem: Engineer) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Engineer, newItem: Engineer) =
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

        fun bind(item: Engineer, onClickListener: OnClickListener) {
            binding.engineer = item
            binding.root.setOnClickListener {
                onClickListener.onClick(item)
            }
        }
    }

    class OnClickListener(private var onClickListener: (Engineer) -> Unit) {
        fun onClick(item: Engineer) = onClickListener(item)
    }
}
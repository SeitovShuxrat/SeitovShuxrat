package com.example.landtech.presentation.ui.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.landtech.data.database.models.ImagesDb
import com.example.landtech.databinding.ImageItemBinding

class ImagesAdapter(private val onClick: (ImagesDb) -> Unit, private val onDelete: (ImagesDb) -> Unit) :
    ListAdapter<ImagesDb, ImagesAdapter.VH>(
        object : DiffUtil.ItemCallback<ImagesDb>() {
            override fun areItemsTheSame(oldItem: ImagesDb, newItem: ImagesDb) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ImagesDb, newItem: ImagesDb) =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageItemBinding.inflate(inflater, parent, false)

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClick, onDelete)
    }

    class VH(private val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImagesDb, onClick: (ImagesDb) -> Unit, onDelete: (ImagesDb) -> Unit) {
            Glide.with(binding.root.context)
                .load(item.imageUri)
                .into(binding.imageView)

            binding.imageView.setOnClickListener {
                onClick(item)
            }
            binding.removeBtn.setOnClickListener {
                onDelete(item)
            }
        }
    }
}
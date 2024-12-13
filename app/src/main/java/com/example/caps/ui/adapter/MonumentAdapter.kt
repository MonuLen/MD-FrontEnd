package com.example.caps.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps.data.model.Monument
import com.example.caps.database.FavoriteMonument
import com.example.caps.databinding.DiscoveredItemBinding

class MonumentAdapter<T>(
    private val onItemClick: (T) -> Unit
) : RecyclerView.Adapter<MonumentAdapter<T>.MonumentViewHolder>() {

    private val items = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonumentViewHolder {
        val binding = DiscoveredItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonumentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonumentViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<T>) {
        items.clear()
        if (newItems != null) {
            items.addAll(newItems)
            Log.d("MonumentAdapter", "Items added: ${newItems.size}")
        } else {
            Log.d("MonumentAdapter", "No items to add")
        }
        notifyDataSetChanged()
    }

    inner class MonumentViewHolder(private val binding: DiscoveredItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                onItemClick(item)
            }
        }

        fun bind(item: T) {
            when (item) {
                is Monument -> {
                    binding.titleText.text = item.name
                    Glide.with(binding.imageView.context)
                        .load(item.image_url)
                        .into(binding.imageView)
                }
                is FavoriteMonument -> {
                    binding.titleText.text = item.name
                    Glide.with(binding.imageView.context)
                        .load(item.image_url)
                        .into(binding.imageView)
                }
            }
        }
    }
}

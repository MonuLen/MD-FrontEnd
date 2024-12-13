package com.example.caps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps.database.FavoriteMonument
import com.example.caps.databinding.FavListBinding

class FavouriteFragmentAdapter(
    private val onItemClick: (FavoriteMonument) -> Unit
) : RecyclerView.Adapter<FavouriteFragmentAdapter.FavouriteViewHolder>() {

    private val items = mutableListOf<FavoriteMonument>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = FavListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<FavoriteMonument>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class FavouriteViewHolder(private val binding: FavListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition])
            }
        }

        fun bind(item: FavoriteMonument) {
            binding.itemTitle.text = item.name
            binding.itemDescription.text = item.history

            // Load image using Glide
            Glide.with(binding.itemImage.context)
                .load(item.image_url)
                .into(binding.itemImage)

            // Handle favorite button click (if needed)
            binding.favButton.setOnClickListener {
                // Add logic for favorite button here

            }
        }
    }
}

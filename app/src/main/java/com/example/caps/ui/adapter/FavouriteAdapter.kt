package com.example.caps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps.database.FavoriteMonument
import com.example.caps.databinding.FavouriteListBinding
import com.example.caps.data.model.Monument

class FavouriteAdapter(
    private val onClick: (Monument) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    private val favorites = mutableListOf<FavoriteMonument>()

    inner class FavouriteViewHolder(private val binding: FavouriteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteMonument) {
            binding.tvItemTitle.text = favorite.name
            binding.tvItemPublishedDate.text = favorite.location
            Glide.with(binding.imgPoster.context)
                .load(favorite.image_url)
                .into(binding.imgPoster)

            binding.root.setOnClickListener { onClick(favorite.toMonument()) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = FavouriteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    fun submitList(newFavorites: List<FavoriteMonument>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
    }
}

// Fungsi konversi dari FavoriteMonument ke Monument
fun FavoriteMonument.toMonument(): Monument {
    return Monument(
        id = this.id,
        name = this.name,
        location = this.location,
        history = this.history,
        image_url = this.image_url,
        created_by = this.created_by ?: listOf("Unknown"), // Gunakan data dari FavoriteMonument atau default
        created_date = this.created_date ?: "Unknown Date", // Gunakan data dari FavoriteMonument atau default
        photos_url = this.photos_url ?: listOf("No Photos") // Gunakan data dari FavoriteMonument atau default
    )
}



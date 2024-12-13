package com.example.caps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caps.R
import com.example.caps.databinding.PhotoItemBinding

class PhotoAdapter(private val photos: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_photo_item)
        fun bind(photoUrl: String) {
            Glide.with(binding.ivPhotoItem.context)
                .load(photoUrl)
                .into(binding.ivPhotoItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
        Glide.with(holder.itemView.context)
            .load(photos[position])
            .placeholder(R.drawable.monas_icon_best_quality)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = photos.size
}

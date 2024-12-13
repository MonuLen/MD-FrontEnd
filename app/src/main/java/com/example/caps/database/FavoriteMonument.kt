package com.example.caps.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_monuments")
data class FavoriteMonument(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val history: String,
    val image_url: String,
    val created_by: List<String>, // Tidak nullable jika harus ada data
    val created_date: String?,    // Nullable jika bisa kosong
    val photos_url: List<String>  // Tidak nullable jika selalu ada data
)
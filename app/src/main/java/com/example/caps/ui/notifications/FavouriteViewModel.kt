package com.example.caps.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caps.database.FavoriteMonument
import com.example.caps.repository.FavoriteMonumentRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: FavoriteMonumentRepository) : ViewModel() {

    val favoriteMonuments: LiveData<List<FavoriteMonument>> = repository.getAllFavorites()

    fun addFavorite(monument: FavoriteMonument) {
        viewModelScope.launch {
            repository.addFavorite(monument) // Perbaikan: gunakan addFavorite
        }
    }

    fun removeFavorite(monument: FavoriteMonument) {
        viewModelScope.launch {
            repository.removeFavorite(monument) // Perbaikan: gunakan removeFavorite
        }
    }
}

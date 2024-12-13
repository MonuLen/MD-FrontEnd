package com.example.caps.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.caps.database.AppDatabase
import com.example.caps.database.FavoriteMonument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteMonumentRepository(application: Application) {

    private val favoriteMonumentDao = AppDatabase.getDatabase(application).favoriteMonumentDao()

    /**
     * Mendapatkan semua data monumen favorit dari database.
     */
    fun getAllFavorites(): LiveData<List<FavoriteMonument>> =
        favoriteMonumentDao.getAllFavorites()

    /**
     * Menambahkan monumen ke daftar favorit.
     */
    suspend fun addFavorite(favoriteMonument: FavoriteMonument) {
        withContext(Dispatchers.IO) {
            favoriteMonumentDao.insert(favoriteMonument)
        }
    }

    /**
     * Menghapus monumen dari daftar favorit.
     */
    suspend fun removeFavorite(favoriteMonument: FavoriteMonument) {
        withContext(Dispatchers.IO) {
            favoriteMonumentDao.delete(favoriteMonument)
        }
    }
}

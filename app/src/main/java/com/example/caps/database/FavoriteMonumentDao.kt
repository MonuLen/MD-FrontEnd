package com.example.caps.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteMonumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteMonument: FavoriteMonument)

    @Delete
    suspend fun delete(favoriteMonument: FavoriteMonument)

    @Query("SELECT * FROM favorite_monuments")
    fun getAllFavorites(): LiveData<List<FavoriteMonument>>
}

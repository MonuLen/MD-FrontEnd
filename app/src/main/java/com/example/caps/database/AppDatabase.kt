package com.example.caps.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteMonument::class],
    version = 2, // Versi database diubah ke 2
    exportSchema = false
)
@TypeConverters(Converters::class) // Tambahkan TypeConverters
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteMonumentDao(): FavoriteMonumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "favorite_monuments_db"
                )
                    .addMigrations(MIGRATION_1_2) // Tambahkan migrasi
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migrasi dari versi 1 ke versi 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tambahkan kolom baru
                database.execSQL("ALTER TABLE favorite_monuments ADD COLUMN created_by TEXT DEFAULT '[]'")
                database.execSQL("ALTER TABLE favorite_monuments ADD COLUMN photos_url TEXT DEFAULT '[]'")
            }
        }
    }
}

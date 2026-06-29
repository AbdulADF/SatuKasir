package com.abduladf.satukasir.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abduladf.satukasir.data.local.dao.CategoryDao
import com.abduladf.satukasir.data.local.dao.ProductDao
import com.abduladf.satukasir.data.local.entity.CategoryEntity
import com.abduladf.satukasir.data.local.entity.ProductEntity

@Database(
    entities = [CategoryEntity::class, ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "satukasir_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
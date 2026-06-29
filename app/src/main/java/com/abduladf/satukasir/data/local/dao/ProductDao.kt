package com.abduladf.satukasir.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abduladf.satukasir.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Perhatikan kita menggunakan Flow<> agar data merespons perubahan secara otomatis (Reactive)
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("UPDATE products SET categoryId = '' WHERE categoryId = :oldCategoryId")
    suspend fun clearCategoryFromProducts(oldCategoryId: String)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
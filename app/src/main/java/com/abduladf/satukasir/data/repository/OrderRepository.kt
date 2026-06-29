package com.abduladf.satukasir.data.repository

import com.abduladf.satukasir.data.local.dao.CategoryDao
import com.abduladf.satukasir.data.local.dao.ProductDao
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepository(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
) {

    // Ambil aliran data kategori secara real-time dan konversi ke Domain Model
    fun getCategoriesStream(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Ambil aliran data produk secara real-time dan konversi ke Domain Model
    fun getProductsStream(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Fungsi untuk menambah/menyimpan kategori baru ke SQLite
    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    // Fungsi untuk menambah/menyimpan produk baru ke SQLite
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.toEntity())
    }

    suspend fun deleteCategory(category: Category) {
        // 1. Lepaskan dulu semua produk yang terhubung dengan kategori ini
        productDao.clearCategoryFromProducts(category.id)

        // 2. Hapus kategorinya dari database
        categoryDao.deleteCategory(category.toEntity())
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}
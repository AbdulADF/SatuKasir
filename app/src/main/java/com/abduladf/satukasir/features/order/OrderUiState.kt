package com.abduladf.satukasir.features.order

import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.OrderItem
import com.abduladf.satukasir.domain.model.Product

data class OrderUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: String? = null, // null artinya "Semua Kategori"
    val cart: List<OrderItem> = emptyList()
) {
    val displayedProducts: List<Product>
        get() = if (selectedCategoryId == null) {
            products
        } else {
            products.filter { it.categoryId == selectedCategoryId }
        }

    val grandTotal: Long
        get() = cart.sumOf { it.subtotal }
}
package com.abduladf.satukasir.data.repository

import com.abduladf.satukasir.data.local.entity.CategoryEntity
import com.abduladf.satukasir.data.local.entity.ProductEntity
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.Product

// Konversi Kategori: Dari Database (Entity) ke UI/Domain (Model)
fun CategoryEntity.toDomain(): Category {
    return Category(id = this.id, name = this.name)
}

// Konversi Kategori: Dari UI/Domain ke Database (Entity)
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(id = this.id, name = this.name)
}

// Konversi Produk: Dari Database (Entity) ke UI/Domain (Model)
fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        categoryId = this.categoryId,
        imageUrl = this.imageUrl
    )
}

// Konversi Produk: Dari UI/Domain ke Database (Entity)
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        categoryId = this.categoryId,
        imageUrl = this.imageUrl
    )
}
package com.abduladf.satukasir.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val categoryId: String,
    val imageUrl: String
)

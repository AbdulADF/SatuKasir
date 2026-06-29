package com.abduladf.satukasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Long,
    val categoryId: String,
    val imageUrl: String
)
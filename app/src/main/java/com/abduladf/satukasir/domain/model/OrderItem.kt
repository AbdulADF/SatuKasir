package com.abduladf.satukasir.domain.model

data class OrderItem(
    val product: Product,
    val quantity: Int = 1
) {
    val subtotal: Long get() = product.price * quantity
}

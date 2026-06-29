package com.abduladf.satukasir.features.order

import android.content.Context
import androidx.lifecycle.ViewModel
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.OrderItem
import com.abduladf.satukasir.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val catMakanan = Category("c1", "Makanan")
        val catMinuman = Category("c2", "Minuman")
        val catCemilan = Category("c3", "Cemilan")
        val catDessert = Category("c4", "Dessert")

        val dummyProducts = listOf(
            Product("p1", "Soto Betawi Daging", 35000, "c1"),
            Product("p2", "Soto Betawi Campur", 35000, "c1"),
            Product("p3", "Nasi Putih", 5000, "c1"),
            Product("p4", "Es Teh Manis", 5000, "c2"),
            Product("p5", "Es Jeruk Peras", 7000, "c2"),
            Product("p6", "Ayam Goreng", 25000, "c1"),
            Product("p7", "Ayam Bakar", 27000, "c1"),
            Product("p8", "Bebek Goreng", 38000, "c1"),
            Product("p9", "Bebek Bakar", 40000, "c1"),
            Product("p10", "Lele Goreng", 18000, "c1"),
            Product("p11", "Lele Bakar", 20000, "c1"),
            Product("p12", "Nasi Goreng Spesial", 28000, "c1"),
            Product("p13", "Nasi Goreng Seafood", 32000, "c1"),
            Product("p14", "Nasi Goreng Kampung", 25000, "c1"),
            Product("p15", "Mie Goreng Jawa", 24000, "c1"),
            Product("p16", "Mie Kuah Ayam", 23000, "c1"),
            Product("p17", "Bakso Urat", 22000, "c1"),
            Product("p18", "Bakso Beranak", 35000, "c1"),
            Product("p19", "Soto Ayam", 22000, "c1"),
            Product("p20", "Soto Lamongan", 24000, "c1"),
            Product("p21", "Rawon", 32000, "c1"),
            Product("p22", "Gudeg Jogja", 30000, "c1"),
            Product("p23", "Rendang", 38000, "c1"),
            Product("p24", "Ayam Geprek", 23000, "c1"),
            Product("p25", "Ayam Penyet", 25000, "c1"),
            Product("p26", "Ikan Gurame Bakar", 55000, "c1"),
            Product("p27", "Ikan Gurame Goreng", 52000, "c1"),
            Product("p28", "Cumi Goreng Tepung", 35000, "c1"),
            Product("p29", "Udang Saus Padang", 42000, "c1"),
            Product("p30", "Capcay", 28000, "c1"),
            Product("p31", "Kangkung Cah", 18000, "c1"),
            Product("p32", "Tumis Tauge", 15000, "c1"),
            Product("p33", "Sayur Asem", 12000, "c1"),
            Product("p34", "Sop Buntut", 55000, "c1"),
            Product("p35", "Sop Iga", 50000, "c1"),
            Product("p36", "Sate Ayam", 30000, "c1"),
            Product("p37", "Sate Kambing", 42000, "c1"),
            Product("p38", "Tongseng Kambing", 45000, "c1"),
            Product("p39", "Gado-Gado", 22000, "c1"),
            Product("p40", "Ketoprak", 18000, "c1"),
            Product("p41", "Pempek Kapal Selam", 32000, "c1"),
            Product("p42", "Pempek Lenjer", 26000, "c1"),
            Product("p43", "Lontong Sayur", 18000, "c1"),
            Product("p44", "Lontong Opor", 28000, "c1"),
            Product("p45", "Bubur Ayam", 18000, "c1"),
            Product("p46", "Nasi Uduk", 20000, "c1"),
            Product("p47", "Nasi Kuning", 22000, "c1"),
            Product("p48", "Mie Ayam Bakso", 25000, "c1"),
            Product("p49", "Mie Ayam Ceker", 26000, "c1"),
            Product("p50", "Mie Ayam Jamur", 27000, "c1"),

            Product("p51", "Es Teh Tawar", 3000, "c2"),
            Product("p52", "Teh Hangat", 4000, "c2"),
            Product("p53", "Es Lemon Tea", 9000, "c2"),
            Product("p54", "Jus Alpukat", 18000, "c2"),
            Product("p55", "Jus Mangga", 17000, "c2"),
            Product("p56", "Jus Melon", 15000, "c2"),
            Product("p57", "Jus Semangka", 15000, "c2"),
            Product("p58", "Jus Jambu", 16000, "c2"),
            Product("p59", "Jus Apel", 17000, "c2"),
            Product("p60", "Jus Wortel", 15000, "c2"),
            Product("p61", "Es Campur", 18000, "c2"),
            Product("p62", "Es Cendol", 15000, "c2"),
            Product("p63", "Es Kelapa Muda", 18000, "c2"),
            Product("p64", "Es Kopi Susu", 22000, "c2"),
            Product("p65", "Americano", 20000, "c2"),
            Product("p66", "Cappuccino", 25000, "c2"),
            Product("p67", "Cafe Latte", 25000, "c2"),
            Product("p68", "Mocha", 28000, "c2"),
            Product("p69", "Chocolate", 22000, "c2"),
            Product("p70", "Matcha Latte", 28000, "c2"),

            Product("p71", "Kentang Goreng", 18000, "c3"),
            Product("p72", "Singkong Goreng", 15000, "c3"),
            Product("p73", "Pisang Goreng", 16000, "c3"),
            Product("p74", "Tahu Crispy", 14000, "c3"),
            Product("p75", "Tempe Mendoan", 15000, "c3"),
            Product("p76", "Cireng", 12000, "c3"),
            Product("p77", "Cilok", 10000, "c3"),
            Product("p78", "Batagor", 18000, "c3"),
            Product("p79", "Siomay", 22000, "c3"),
            Product("p80", "Otak-Otak", 18000, "c3"),
            Product("p81", "Roti Bakar Coklat", 18000, "c3"),
            Product("p82", "Roti Bakar Keju", 18000, "c3"),
            Product("p83", "Roti Bakar Coklat Keju", 22000, "c3"),
            Product("p84", "Pisang Bakar", 18000, "c3"),
            Product("p85", "Sosis Bakar", 15000, "c3"),

            Product("p86", "Pudding Coklat", 12000, "c4"),
            Product("p87", "Pudding Vanilla", 12000, "c4"),
            Product("p88", "Ice Cream Vanilla", 15000, "c4"),
            Product("p89", "Ice Cream Chocolate", 15000, "c4"),
            Product("p90", "Ice Cream Strawberry", 15000, "c4"),
            Product("p91", "Brownies", 18000, "c4"),
            Product("p92", "Cheesecake", 28000, "c4"),
            Product("p93", "Tiramisu", 30000, "c4"),
            Product("p94", "Donat Coklat", 10000, "c4"),
            Product("p95", "Donat Keju", 10000, "c4"),
            Product("p96", "Donat Glaze", 10000, "c4"),
            Product("p97", "Klepon", 12000, "c4"),
            Product("p98", "Lapis Legit", 25000, "c4"),
            Product("p99", "Kue Cubit", 15000, "c4"),
            Product("p100", "Martabak Mini", 18000, "c4"),
        )

        _uiState.update {
            it.copy(
                categories = listOf(catMakanan, catMinuman, catCemilan, catDessert),
                products = dummyProducts
            )
        }
    }

    fun selectCategory(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun addToCart(product: Product) {
        _uiState.update { state ->
            val mutableCart = state.cart.toMutableList()
            val existingIndex = mutableCart.indexOfFirst { it.product.id == product.id }

            if (existingIndex != -1) {
                // Jika barang sudah ada di summary kanan, tambah Qty +1
                val item = mutableCart[existingIndex]
                mutableCart[existingIndex] = item.copy(quantity = item.quantity + 1)
            } else {
                // Jika belum ada, masukkan barang baru dengan Qty 1
                mutableCart.add(OrderItem(product = product, quantity = 1))
            }

            state.copy(cart = mutableCart)
        }
    }

    fun reduceFromCart(product: Product) {
        _uiState.update { state ->
            val mutableCart = state.cart.toMutableList()
            val existingIndex = mutableCart.indexOfFirst { it.product.id == product.id }

            if (existingIndex != -1) {
                val item = mutableCart[existingIndex]
                if (item.quantity > 1) {
                    mutableCart[existingIndex] = item.copy(quantity = item.quantity - 1)
                } else {
                    mutableCart.removeAt(existingIndex) // Kalau Qty sisa 1 dikurangi, hapus dari list
                }
            }

            state.copy(cart = mutableCart)
        }
    }

    fun printCurrentOrder(context: Context) {
        // Nanti panggil ThermalPrinterHelper kamu di sini mengirimkan data state.cart!
        println("Mencetak pesanan sejumlah: ${_uiState.value.cart.size} item")
    }
}
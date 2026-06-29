package com.abduladf.satukasir.features.order

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abduladf.satukasir.data.repository.OrderRepository
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.OrderItem
import com.abduladf.satukasir.domain.model.Product
import com.abduladf.satukasir.utils.ThermalPrinterHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeDatabase()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.getCategoriesStream().collect { categoriesList ->
                _uiState.update { it.copy(categories = categoriesList) }
                if (categoriesList.isEmpty()) seedInitialDummyData()
            }
        }

        viewModelScope.launch {
            repository.getProductsStream().collect { productsList ->
                _uiState.update { it.copy(products = productsList) }
            }
        }
    }

    // =====================================================================
    // THE REAL DEAL: LOGIKA PRINT & PEMBUATAN STRUK
    // =====================================================================

    /**
     * @param cart List barang yang ingin dicetak (Bisa keranjang aktif, bisa data riwayat)
     */
    fun printCurrentOrder(
        context: Context,
        cart: List<OrderItem>,
        timestamp: Long = System.currentTimeMillis()
    ) {
        if (cart.isEmpty() || _uiState.value.isPrinting) return

        _uiState.update { it.copy(isPrinting = true) }

        // MENGIRIM DATA BLUETOOTH WAJIB DI IO THREAD AGAR UI TIDAK FREEZE!
        viewModelScope.launch(Dispatchers.IO) {
            val receiptContent = generateEscPosReceipt(cart, timestamp)
            val printResult = ThermalPrinterHelper.printReceiptWithLogo(context, receiptContent)

            printResult.onSuccess {
                // Jika sukses keluar kertas, otomatis kosongkan keranjang belanja di layar utama!
                _uiState.update { it.copy(cart = emptyList(), isPrinting = false) }
            }.onFailure { error ->
                // Lompat kembali ke UI Thread untuk menampilkan Toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Printer Error: ${error.message ?: "Periksa koneksi Bluetooth"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                _uiState.update { it.copy(isPrinting = false) }
            }
        }
    }

    /**
     * Merakit string biner struk format 58mm (Maksimal 32 Karakter per baris)
     */
    private fun generateEscPosReceipt(cart: List<OrderItem>, timestamp: Long): String {
        val sb = StringBuilder()

        val dateObj = Date(timestamp)
        val localeID = Locale("id", "ID")

        val dateOnlyStr = SimpleDateFormat("dd/MM/yyyy", localeID).format(dateObj) // Output: "29/06/2026"
        val timeOnlyStr = SimpleDateFormat("HH:mm", localeID).format(dateObj)     // Output: "23:16"

        // 1. Header Toko (Logo di atasnya sudah di-handle otomatis oleh ThermalPrinterHelper)
        sb.append("[C]<b>SOTO BETAWI & WARKOP MPOK NOER</b>\n")
        sb.append("[C]Jl. Raya Bojong Gede\n")
        sb.append("[C]Bogor, Jawa Barat\n")
        sb.append("[L]\n")
        sb.append("[L]$dateOnlyStr[R]Waktu: $timeOnlyStr\n")
        sb.append("[C]================================\n")

        var totalBelanja = 0L

        // 2. Looping Item Pesanan
        cart.forEach { item ->
            val name = item.product.name
            val qty = item.quantity
            val priceStr = item.product.price.formatIDR()
            val subtotalStr = item.subtotal.formatIDR()
            totalBelanja += item.subtotal

            // Trik Standar Struk 58mm:
            // Baris 1: Nama Menu (Biarkan memanjang ke bawah jika namanya panjang)
            sb.append("[L]$name\n")
            // Baris 2: [Rata Kiri] Qty x Harga  ----------  [Rata Kanan] Subtotal
            sb.append("[L]  $qty x $priceStr[R]$subtotalStr\n")
        }

        // 3. Footer Kalkulasi
        sb.append("[C]--------------------------------\n")
        sb.append("[L]<b>TOTAL</b>[R]<b>Rp ${totalBelanja.formatIDR()}</b>\n")
        sb.append("[C]================================\n")
        sb.append("[C]Dibuat Dengan Cinta,\n")
        sb.append("[C]Rasanya Sampai ke Hati!\n")

        return sb.toString()
    }

    // =====================================================================
    // LOGIKA CRUD & CART LAINNYA
    // =====================================================================

    fun addNewCategory(name: String) {
        viewModelScope.launch {
            val uniqueId = "c_${System.currentTimeMillis()}"
            repository.insertCategory(Category(id = uniqueId, name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (_uiState.value.selectedCategoryId == category.id) selectCategory(null)
        }
    }

    fun saveProduct(id: String?, name: String, price: Long, categoryId: String, imageUrl: String) {
        viewModelScope.launch {
            val finalId = id ?: "p_${System.currentTimeMillis()}"
            repository.insertProduct(Product(finalId, name, price, categoryId, imageUrl))
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    fun selectCategory(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun addToCart(product: Product) {
        _uiState.update { state ->
            val mutableCart = state.cart.toMutableList()
            val existingIndex = mutableCart.indexOfFirst { it.product.id == product.id }
            if (existingIndex != -1) {
                val item = mutableCart[existingIndex]
                mutableCart[existingIndex] = item.copy(quantity = item.quantity + 1)
            } else {
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
                    mutableCart.removeAt(existingIndex)
                }
            }
            state.copy(cart = mutableCart)
        }
    }

    private suspend fun seedInitialDummyData() {
        val catMakanan = Category("c1", "Makanan")
        val catMinuman = Category("c2", "Minuman")
        repository.insertCategory(catMakanan)
        repository.insertCategory(catMinuman)

        listOf(
            Product("p1", "Soto Betawi Daging", 35000, "c1", ""),
            Product("p4", "Es Teh Manis", 5000, "c2", ""),
        ).forEach { repository.insertProduct(it) }
    }

    // Helper pemercantik angka mata uang (35000 -> "35.000")
    private fun Long.formatIDR(): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID")).format(this)
    }
}
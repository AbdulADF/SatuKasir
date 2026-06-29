package com.abduladf.satukasir.features.settings.menuEditor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abduladf.satukasir.data.local.database.AppDatabase
import com.abduladf.satukasir.data.repository.OrderRepository
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.Product
import com.abduladf.satukasir.features.order.OrderViewModel
import com.abduladf.satukasir.features.order.OrderViewModelFactory
import com.abduladf.satukasir.features.order.ProductCardItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuEditorScreen(
    onAddNewProduct: () -> Unit = {}   // Nanti diisi aksi pindah ke AddProductScreen
) {
    val context = LocalContext.current

    // Injeksi manual menggunakan Factory dan Room Database
    val viewModel: OrderViewModel = viewModel(
        factory = remember {
            val database = AppDatabase.getDatabase(context)
            val repository = OrderRepository(database.categoryDao(), database.productDao())
            OrderViewModelFactory(repository)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    var showProductDialog by remember { mutableStateOf(false) }
    var selectedProductForEdit by remember { mutableStateOf<Product?>(null) } // null = Create, ada isi = Edit
    var productToDelete by remember { mutableStateOf<Product?>(null) } // Untuk quick delete via long-press

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Menu Editor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically // <--- Kunci agar semua chip sejajar horisontal
        ) {
            // TOMBOL 1: Tambah Kategori (Mode Aksi)
            item {
                PosCategoryChip(
                    text = "Tambah Kategori",
                    isAction = true,
                    leadingIcon = {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    onClick = { showAddCategoryDialog = true }
                )
            }

            // TOMBOL 2: Filter Semua
            item {
                PosCategoryChip(
                    text = "Semua",
                    isSelected = uiState.selectedCategoryId == null,
                    onClick = { viewModel.selectCategory(null) }
                )
            }

            // TOMBOL 3..N: Filter Kategori Dinamis (+ Long Press Hapus)
            items(uiState.categories) { category ->
                PosCategoryChip(
                    text = category.name,
                    isSelected = uiState.selectedCategoryId == category.id,
                    onClick = { viewModel.selectCategory(category.id) },
                    onLongClick = { categoryToDelete = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AddNewProductCard(onClick = {
                    selectedProductForEdit = null
                    showProductDialog = true
                })
            }
            items(uiState.displayedProducts) { product ->
                ProductCardItem(
                    product = product,
                    onClick = {
                        selectedProductForEdit = product // Isi dengan barang yang diklik
                        showProductDialog = true
                    },
                    onLongClick = {
                        productToDelete = product
                    }
                )
            }
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            onConfirm = { newCategoryName ->
                // Panggil fungsi di ViewModel untuk simpan ke Room Database
                viewModel.addNewCategory(newCategoryName)

                // Tutup dialog setelah disimpan
                showAddCategoryDialog = false
            }
        )
    }

    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Hapus Kategori?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Apakah Anda yakin ingin menghapus kategori '${categoryToDelete?.name}'?\n\nItem yang ada di dalam kategori ini tidak akan ikut terhapus, melainkan menjadi 'Tanpa Kategori' (Hanya muncul di tab Semua).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        categoryToDelete?.let { viewModel.deleteCategory(it) }
                        categoryToDelete = null // Tutup dialog
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showProductDialog) {
        ProductFormDialog(
            productToEdit = selectedProductForEdit,
            categories = uiState.categories,
            activeCategoryId = uiState.selectedCategoryId,
            onDismiss = { showProductDialog = false },
            onSave = { id, name, price, catId, img ->
                viewModel.saveProduct(id, name, price, catId, img)
            },
            onDelete = {
                selectedProductForEdit?.let { viewModel.deleteProduct(it) }
            }
        )
    }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Hapus Menu?", fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus '${productToDelete?.name}' dari daftar menu?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { viewModel.deleteProduct(it) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun AddNewProductCard(onClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val cornerRadius = 12.dp

    val dashedStroke = remember {
        Stroke(
            width = 4f,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(20f, 15f),
                phase = 0f
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Pastikan tinggi ini sama dengan tinggi ProductCardItem kamu yang sekarang
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = primaryColor,
                    style = dashedStroke,
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Menu Baru",
                tint = primaryColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tambah Menu",
                color = primaryColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // State untuk menyimpan teks yang diketik user
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Tambah Kategori Baru", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Nama Kategori") },
                placeholder = { Text("Misal: Snack, Minuman Dingin") },
                singleLine = true,
                // Otomatis membuat huruf pertama menjadi kapital
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    // Hilangkan spasi berlebih di awal/akhir, lalu kirim
                    onConfirm(categoryName.trim())
                },
                // Tombol simpan non-aktif (mati) jika inputan kosong
                enabled = categoryName.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}

/**
 * Komponen Universal untuk seluruh Chips di aplikasi SatuKasir.
 * Menjamin tinggi, padding, font-baseline, dan border rata presisi 100%.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PosCategoryChip(
    text: String,
    isSelected: Boolean = false,
    isAction: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    // Penentuan Warna Latar
    val containerColor = when {
        isAction -> MaterialTheme.colorScheme.primaryContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    // Penentuan Warna Teks & Ikon
    val contentColor = when {
        isAction -> MaterialTheme.colorScheme.onPrimaryContainer
        isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    // Penentuan Garis Pinggir (Hanya muncul kalau tidak dipilih & bukan tombol aksi)
    val borderStroke = if (!isSelected && !isAction) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) // outlineVariant lebih lembut di mata
    } else null

    Surface(
        modifier = Modifier
            .height(36.dp) // Sengaja dibuat 36dp (standar M3 32dp), agar lebih nyaman di jari kasir tablet
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick ?: {}
            ),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically, // Memaksa teks & ikon berada tepat di tengah vertikal
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected || isAction) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    productToEdit: Product? = null, // null = Mode Create Baru
    categories: List<Category>,
    activeCategoryId: String? = null,
    onDismiss: () -> Unit,
    onSave: (id: String?, name: String, price: Long, categoryId: String, imageUrl: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isEditMode = productToEdit != null

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var priceStr by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var selectedCategoryId by remember {
        mutableStateOf(
            productToEdit?.categoryId             // 1. Jika mode edit, prioritaskan kategori bawaan produk
                ?: activeCategoryId                   // 2. Jika buat baru, ikuti chip kategori yang sedang menyala
                ?: categories.firstOrNull()?.id       // 3. Jika chip menyala di "Semua" (null), pilih kategori urutan pertama
                ?: ""                                 // 4. Fallback jika list kategori masih benar-benar kosong
        )
    }
    var imageUrl by remember { mutableStateOf(productToEdit?.imageUrl ?: "") }

    // State untuk Dropdown Kategori
    var expandedCategory by remember { mutableStateOf(false) }
    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Pilih Kategori"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditMode) "Edit Menu" else "Tambah Menu Baru", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 1. Input Nama
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Menu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Input Harga
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { input ->
                        // Filter hanya menerima ketikan angka
                        if (input.all { it.isDigit() }) priceStr = input
                    },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Dropdown Pilih Kategori
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCategoryId = cat.id
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                // 4. Info Foto (Sementara pakai path teks sebelum terhubung ke ImageHelper Galeri)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Path File Foto (Opsional)") },
                    placeholder = { Text("/storage/emulated/0/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceLong = priceStr.toLongOrNull() ?: 0L
                    onSave(productToEdit?.id, name.trim(), priceLong, selectedCategoryId, imageUrl)
                    onDismiss()
                },
                enabled = name.isNotBlank() && priceStr.isNotBlank() && selectedCategoryId.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Munculkan tombol Hapus hanya jika sedang dalam Mode Edit!
                if (isEditMode && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
            }
        }
    )
}
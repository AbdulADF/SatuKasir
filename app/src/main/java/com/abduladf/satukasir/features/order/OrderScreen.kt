package com.abduladf.satukasir.features.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abduladf.satukasir.data.local.database.AppDatabase
import com.abduladf.satukasir.data.repository.OrderRepository
import com.abduladf.satukasir.domain.model.Category
import com.abduladf.satukasir.domain.model.OrderItem
import com.abduladf.satukasir.domain.model.Product
import com.abduladf.satukasir.utils.PrinterStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderScreen(
    printerStatus: PrinterStatus
) {
    val context = LocalContext.current

    val isPrinterReady = printerStatus is PrinterStatus.Connected || printerStatus is PrinterStatus.Checking

    val viewModel: OrderViewModel = viewModel(
        factory = remember {
            val database = AppDatabase.getDatabase(context)
            val repository = OrderRepository(database.categoryDao(), database.productDao())
            OrderViewModelFactory(repository)
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text("Pilih Menu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryId == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text("Semua") }
                    )
                }
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == category.id,
                        onClick = { viewModel.selectCategory(category.id) },
                        label = { Text(category.name) }
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
                items(uiState.displayedProducts) { product ->
                    ProductCardItem(
                        product = product,
                        onClick = { viewModel.addToCart(product) }
                    )
                }
            }
        }

        VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text("Detail Pesanan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.cart.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Belum ada item dipilih", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.cart) { item ->
                        CartItemRow(
                            item = item,
                            onAdd = { viewModel.addToCart(item.product) },
                            onReduce = { viewModel.reduceFromCart(item.product) }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(uiState.grandTotal.formatRupiah(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.printCurrentOrder(context, uiState.cart) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.cart.isNotEmpty() && !uiState.isPrinting && isPrinterReady
            ) {
                if (uiState.isPrinting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mencetak...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    // Teks dinamis sesuai status dari SatuKasirApp!
                    Text(
                        text = if (!isPrinterReady) "Printer Terputus" else "Cetak Struk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCardItem(
    product: Product,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .height(110.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.price.formatRupiah(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CartItemRow(item: OrderItem, onAdd: () -> Unit, onReduce: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${item.product.price.formatRupiah()} x ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onReduce, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
            }
            Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
            IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
            }
        }

        Text(item.subtotal.formatRupiah(), modifier = Modifier.width(85.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End, fontWeight = FontWeight.SemiBold)
    }
}

private fun Long.formatRupiah(): String {
    val formatter = NumberFormat.getInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(this)}"
}
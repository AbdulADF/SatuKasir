package com.abduladf.satukasir

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abduladf.satukasir.navigation.AppNavigation
import com.abduladf.satukasir.navigation.bottomNavItems
import com.abduladf.satukasir.utils.PrinterStatus
import com.abduladf.satukasir.utils.ThermalPrinterHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SatuKasirApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    var showWarningDialog by remember { mutableStateOf(false) }
    var printerStatus by remember { mutableStateOf<PrinterStatus>(PrinterStatus.Checking) }

    LaunchedEffect(Unit) {
        while (true) {
            withContext(Dispatchers.IO) {
                val currentStatus = ThermalPrinterHelper.checkPrinterConnection()
                withContext(Dispatchers.Main) {
                    printerStatus = currentStatus
                }
            }
            delay(30000) // Cek ulang setiap 30 detik sekali
        }
    }

    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        emptyList()
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = bluetoothPermissions)
    LaunchedEffect(Unit) {
        if (bluetoothPermissions.isNotEmpty() && !permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- BAR STATUS PRINTER DI BAGIAN PALING ATAS ---
        val barColor = when (printerStatus) {
            is PrinterStatus.Connected -> Color(0xFF4CAF50) // Hijau
            is PrinterStatus.Disconnected -> Color(0xFFF44336) // Merah
            is PrinterStatus.Checking -> Color(0xFFFF9800) // Oranye
            is PrinterStatus.Error -> Color(0xFF757575) // Abu-abu
        }

        val statusText = when (printerStatus) {
            is PrinterStatus.Connected -> "Printer Terhubung"
            is PrinterStatus.Disconnected -> "Printer Terputus / Mati"
            is PrinterStatus.Checking -> "Memeriksa Printer..."
            is PrinterStatus.Error -> "Printer Eror"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(barColor)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelMedium
            )
        }

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                val currentRoute = navBackStackEntry?.destination?.route
                bottomNavItems.forEach { destination ->
                    item(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, null) },
                        label = { Text(destination.title) }
                    )
                }
            }
        ) {
            AppNavigation(navController)
        }
    }

    if (showWarningDialog || (bluetoothPermissions.isNotEmpty() && !permissionsState.allPermissionsGranted && permissionsState.shouldShowRationale)) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Izin Bluetooth & Scan Dibutuhkan") },
            text = { Text("SatuKasir memerlukan akses Bluetooth dan Scan untuk mendeteksi serta menghentikan penemuan perangkat (discovery) pada thermal printer Anda.") },
            confirmButton = {
                Button(
                    onClick = {
                        showWarningDialog = false
                        permissionsState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Izinkan Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
package com.abduladf.satukasir

import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abduladf.satukasir.navigation.AppNavigation
import com.abduladf.satukasir.navigation.bottomNavItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SatuKasirApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    var showWarningDialog by remember { mutableStateOf(false) }

    // 1. Definisikan daftar izin bluetooth yang dibutuhkan untuk Android 12 ke atas (API 31+)
    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        emptyList()
    }

    // 2. Ubah dari single permission tracker menjadi multiple permissions tracker
    val permissionsState = rememberMultiplePermissionsState(permissions = bluetoothPermissions)

    // 3. Minta bundel izin langsung saat aplikasi dibuka
    LaunchedEffect(Unit) {
        if (bluetoothPermissions.isNotEmpty() && !permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
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

    // 4. Update Dialog edukasi agar merefleksikan kebutuhan kedua izin tersebut
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
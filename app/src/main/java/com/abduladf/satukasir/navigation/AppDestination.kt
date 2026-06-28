package com.abduladf.satukasir.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : AppDestination(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Order : AppDestination(
        "order",
        "Order Baru",
        Icons.Default.ShoppingCart
    )

    object MenuEditor : AppDestination(
        "menuEditor",
        "Menu Editor",
        Icons.AutoMirrored.Filled.List
    )

    object Sandbox: AppDestination(
        "sandbox",
        "Sandbox",
        Icons.Default.Star
    )

    object Settings : AppDestination(
        "settings",
        "Settings",
        Icons.AutoMirrored.Filled.List
    )
}
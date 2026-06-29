package com.abduladf.satukasir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abduladf.satukasir.features.home.HomeScreen
import com.abduladf.satukasir.features.order.OrderScreen
import com.abduladf.satukasir.features.sandbox.SandboxScreen
import com.abduladf.satukasir.features.settings.menuEditor.MenuEditorScreen
import com.abduladf.satukasir.utils.PrinterStatus

@Composable
fun AppNavigation(
    navController: NavHostController,
    printerStatus: PrinterStatus
) {

    NavHost(
        navController = navController,
        startDestination = AppDestination.Order.route,
    ) {
        composable(AppDestination.Order.route) {
            OrderScreen(printerStatus = printerStatus)
        }

        composable(AppDestination.MenuEditor.route) {
            MenuEditorScreen()
        }
    }
}
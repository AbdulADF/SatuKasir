package com.abduladf.satukasir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abduladf.satukasir.features.home.HomeScreen
import com.abduladf.satukasir.features.order.OrderScreen
import com.abduladf.satukasir.features.sandbox.SandboxScreen
import com.abduladf.satukasir.features.settings.menuEditor.MenuEditorScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {

        composable(AppDestination.Home.route) {
            HomeScreen()
        }

        composable(AppDestination.Order.route) {
            OrderScreen()
        }

        composable(AppDestination.MenuEditor.route) {
            MenuEditorScreen()
        }

        composable(AppDestination.Sandbox.route) {
            SandboxScreen()
        }
    }
}
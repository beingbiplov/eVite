package com.example.evite.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun RequireAuth(
    navController: NavController,
    isLoggedIn: Boolean,
    content: @Composable () -> Unit
) {
    val currentRoute = navController.currentDestination?.route

    if (!isLoggedIn) {
        if (currentRoute != NavRoutes.Login.route) {
            navController.navigate(NavRoutes.Login.route) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    } else {
        content()
    }
}

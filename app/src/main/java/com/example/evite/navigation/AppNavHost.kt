package com.example.evite.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.evite.ui.screens.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Home.route) { HomeScreen() }
        composable(NavRoutes.CreateEvent.route) { CreateEventScreen() }
        composable(NavRoutes.Profile.route) { ProfileScreen() }
    }
}

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
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavRoutes.Home.route) },
                onRegisterClick = { navController.navigate(NavRoutes.Register.route) }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(NavRoutes.Login.route) },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen()
        }

        composable(route = NavRoutes.CreateEvent.route) {
            CreateEventScreen()
        }

        composable(route= NavRoutes.Profile.route) {
            ProfileScreen()
        }
    }
}

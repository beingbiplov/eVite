package com.example.evite.navigation

import androidx.compose.ui.Modifier
import com.example.evite.ui.screens.*
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import com.example.evite.ui.viewmodels.UserViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn = userViewModel.isLoggedIn.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) NavRoutes.Home.route else NavRoutes.Login.route,
        modifier = modifier
    ) {

        // -------------------- LOGIN -------------------------
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = userViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(NavRoutes.Register.route)
                }
            )
        }

        // -------------------- REGISTER -------------------------
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = userViewModel,
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // -------------------- HOME (protected) -------------------------
        composable(NavRoutes.Home.route) {
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                HomeScreen(
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }

        // -------------------- CREATE EVENT -------------------------
        composable(NavRoutes.CreateEvent.route) {
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                CreateEventScreen()
            }
        }

        // -------------------- PROFILE -------------------------
        composable(NavRoutes.Profile.route) {
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                ProfileScreen(
                    viewModel = userViewModel,
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(0)
                        }
                    },
                    onEditProfile = {
                        println("Edit Profile clicked")
                    }
                )
            }
        }
    }
}

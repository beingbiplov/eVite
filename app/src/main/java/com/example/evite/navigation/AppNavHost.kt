package com.example.evite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.evite.ui.screens.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = Modifier.padding(padding)
    ) {

        composable("home") {
            HomeScreen()
        }
//
//        composable(NavRoutes.EventsList.route) {
//            EventsListScreen(navController)
//        }
//
//        composable(NavRoutes.CreateEvent.route) {
//            CreateEventScreen(navController)
//        }
//
//        composable(NavRoutes.Profile.route) {
//            ProfileScreen(navController)
//        }
//
//        composable(NavRoutes.EditProfile.route) {
//            EditProfileScreen(navController)
//        }
//
//        // Auth
//        composable(NavRoutes.Login.route) {
//            LoginScreen(navController)
//        }
//
//        composable(NavRoutes.Register.route) {
//            RegisterScreen(navController)
//        }
//
//        composable(NavRoutes.ForgotPassword.route) {
//            ForgotPasswordScreen(navController)
//        }
//
//        composable(NavRoutes.ResetPassword.route) {
//            ResetPasswordScreen(navController)
//        }
//
//        // Dynamic route for event details
//        composable(NavRoutes.EventDetails.route) { backStackEntry ->
//            val id = backStackEntry.arguments?.getString("eventId")?.toInt() ?: 0
//            EventDetailsScreen(navController, id)
//        }
    }
}

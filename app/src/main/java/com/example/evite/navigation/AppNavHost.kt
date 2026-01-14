package com.example.evite.navigation

import androidx.navigation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.evite.ui.screens.*
import com.example.evite.ui.viewmodels.UserViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    // Auto redirect after login
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {

        // -------------------- LOGIN -------------------------
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = userViewModel,
                onLoginSuccess = {},
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

        // -------------------- HOME -------------------------
        composable(NavRoutes.Home.route) {
            RequireAuth(navController, isLoggedIn) {
                HomeScreen(
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
                    },
                    onCreateEventClick = {
                        navController.navigate(NavRoutes.CreateEvent.route)
                    },
                    onEventClick = { event ->
                        navController.navigate(NavRoutes.EventDetails.createRoute(event.id))
                    }
                )
            }
        }

        // -------------------- CREATE EVENT -------------------------
        composable(
            route = NavRoutes.CreateEvent.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()

            RequireAuth(navController, isLoggedIn) {
                CreateEventScreen(
                    eventId = eventId,
                    onEventCreated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    onAddInviteeClick = {
                        navController.navigate(NavRoutes.AddEmails.route)
                    }
                )
            }
        }

        // -------------------- ADD EMAILS -------------------------
        composable(NavRoutes.AddEmails.route) {
            RequireAuth(navController, isLoggedIn) {
                SendInviteScreen(
                    onBack = { navController.popBackStack() },
                    onInvite = { _, _ ->
                        navController.popBackStack()
                    }
                )
            }
        }

        // -------------------- PROFILE -------------------------
        composable(NavRoutes.Profile.route) {
            RequireAuth(navController, isLoggedIn) {
                ProfileScreen(
                    viewModel = userViewModel,
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
                    },
                    onEditProfile = {
                        navController.navigate("edit_profile")
                    }
                )
            }
        }

        // -------------------- EDIT PROFILE -------------------------
        composable("edit_profile") {
            RequireAuth(navController, isLoggedIn) {
                EditProfileScreen(
                    viewModel = userViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // -------------------- EVENT DETAILS -------------------------
        composable(
            route = NavRoutes.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0

            RequireAuth(navController, isLoggedIn) {
                EventDetailsScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { event ->
                        navController.navigate(NavRoutes.CreateEvent.createRoute(event.id))
                    }
                )
            }
        }
    }
}

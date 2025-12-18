package com.example.evite.navigation

import androidx.compose.ui.Modifier
import com.example.evite.ui.screens.*
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(NavRoutes.CreateEvent.route) {
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                CreateEventScreen(
                    onEventCreated = {
                        // After creating event, maybe go home or details?
                        // User previously said "route to add email page" after save,
                        // but now implies "add invitee" button does that.
                        // For now keep previous logic or just pop back to Home as per latest conversation hint "return into home".
                        // Wait, user said "save event button clicked... return into home". 
                        // So I will change this to popBackStack which goes to Home.
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onAddInviteeClick = {
                         navController.navigate(NavRoutes.AddEmails.route)
                    }
                )
            }
        }

        // -------------------- ADD EMAILS -------------------------
        composable(NavRoutes.AddEmails.route) {
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                SendInviteScreen(
                    onBack = { navController.popBackStack() },
                    onInvite = { email, name ->
                         // TODO: Handle adding invitee to ViewModel list
                         navController.popBackStack()
                    }
                )
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
                        navController.navigate("edit_profile")
                    }
                )
            }
        }

        // -------------------- PROFILE UPDATE -------------------------
        composable("edit_profile") {
            EditProfileScreen(
                viewModel = userViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // -------------------- EVENT DETAILS -------------------------
        composable(
            route = NavRoutes.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            if (!isLoggedIn) {
                navController.navigate(NavRoutes.Login.route) { popUpTo(0) }
            } else {
                EventDetailsScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

    }
}

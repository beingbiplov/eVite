package com.example.evite.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.evite.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun BottomNavBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == NavRoutes.Home.route,
            onClick = {
                navController.navigate(NavRoutes.Home.route) {
                    launchSingleTop = true
                }
            },
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.CreateEvent.route,
            onClick = {
                navController.navigate(NavRoutes.CreateEvent.route) {
                    launchSingleTop = true
                }
            },
            label = { Text("Create") },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.Profile.route,
            onClick = {
                navController.navigate(NavRoutes.Profile.route) {
                    launchSingleTop = true
                }
            },
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )
    }
}

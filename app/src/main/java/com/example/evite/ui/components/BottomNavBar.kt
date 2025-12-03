package com.example.evite.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.example.evite.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*


@Composable
fun BottomNavBar(navController: NavHostController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(NavRoutes.Home.route) },
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(NavRoutes.CreateEvent.route) },
            label = { Text("Create") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(NavRoutes.Profile.route) },
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") }
        )
    }
}

package com.example.evite

import android.os.Bundle
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.evite.navigation.NavRoutes
import com.example.evite.navigation.AppNavHost
import com.example.evite.ui.components.AppHeader
import com.example.evite.ui.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import com.example.evite.ui.viewmodels.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EviteApp()
        }
    }
}

@Composable
fun EviteApp() {

    val navController = rememberNavController()

    val userViewModel: UserViewModel = viewModel()

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    // Hide bars on login & register screens
    val hideBars = currentRoute == NavRoutes.Login.route ||
            currentRoute == NavRoutes.Register.route

    Scaffold(
        topBar = { if (!hideBars) AppHeader() },
        bottomBar = { if (!hideBars) BottomNavBar(navController) }
    ) { paddingValues ->

        AppNavHost(
            navController = navController,
            userViewModel = userViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

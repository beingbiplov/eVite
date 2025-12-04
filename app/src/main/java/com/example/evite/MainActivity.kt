package com.example.evite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.evite.navigation.AppNavHost
import com.example.evite.navigation.NavRoutes
import com.example.evite.ui.components.BottomNavBar
import com.example.evite.ui.components.AppHeader

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
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val hideBars = currentRoute == NavRoutes.Login.route ||
            currentRoute == NavRoutes.Register.route

    Scaffold(
        topBar = { if (!hideBars) AppHeader() },
        bottomBar = { if (!hideBars) BottomNavBar(navController) }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

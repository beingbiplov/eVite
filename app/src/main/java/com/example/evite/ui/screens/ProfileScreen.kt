package com.example.evite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evite.ui.viewmodels.UserViewModel

@Composable
fun ProfileScreen(
    viewModel: UserViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile Screen")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            viewModel.logout()
            onLogout()
        }) {
            Text("Logout")
        }
    }
}

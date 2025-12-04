package com.example.evite.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "eVite",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

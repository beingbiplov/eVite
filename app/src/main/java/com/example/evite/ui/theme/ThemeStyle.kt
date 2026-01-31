package com.example.evite.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun getThemeStyle(theme: String): Pair<List<Color>, ImageVector> {
    return when (theme.lowercase()) {
        "party" -> Pair(
            listOf(Color(0xFFF43F5E), Color(0xFFEC4899)),
            Icons.Default.Cake
        )
        "business" -> Pair(
            listOf(Color(0xFF0EA5E9), Color(0xFF2563EB)),
            Icons.Default.BusinessCenter
        )
        "casual" -> Pair(
            listOf(Color(0xFF10B981), Color(0xFF059669)),
            Icons.Default.Coffee
        )
        "formal" -> Pair(
            listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
            Icons.Default.AutoAwesome
        )
        "wedding" -> Pair(
            listOf(Color(0xFFEC4899), Color(0xFFD946EF)),
            Icons.Default.Favorite
        )
        else -> Pair(
            listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
            Icons.Default.Celebration
        )
    }
}

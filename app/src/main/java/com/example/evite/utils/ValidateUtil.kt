package com.example.evite.utils

object ValidationUtil {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // At least 6 characters and must include at least one number.
        // Accepts letters, numbers, and special symbols.
        val regex = Regex("^(?=.*\\d).{6,}$")
        return regex.matches(password)
    }
}

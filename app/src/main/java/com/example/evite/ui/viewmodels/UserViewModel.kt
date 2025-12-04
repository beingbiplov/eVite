package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.repository.UserRepository
import com.example.evite.utils.HashUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val repo = UserRepository(userDao)

    val loginState = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState = _registerState.asStateFlow()

    val isLoggedIn = MutableStateFlow(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true

            val hashed = HashUtil.sha256(password)
            val user = userDao.login(email, hashed)

            isLoading.value = false

            if (user != null) {
                loginState.value = "success"
                isLoggedIn.value = true
            } else {
                loginState.value = "Invalid email or password"
            }
        }
    }

    fun logout() {
        loginState.value = null
        isLoggedIn.value = false
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repo.registerUser(fullName, email, password)
            _registerState.value = if (result > 0) "success" else "Failed to register"
        }
    }
}

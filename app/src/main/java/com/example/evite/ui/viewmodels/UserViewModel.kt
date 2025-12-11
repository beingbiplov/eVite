package com.example.evite.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.local.entities.User
import com.example.evite.data.repository.UserRepository
import com.example.evite.utils.HashUtil
import com.example.evite.utils.ValidationUtil
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

    val loggedInUser = MutableStateFlow<User?>(null)


    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (!ValidationUtil.isValidEmail(email)) {
                loginState.value = "Invalid email format"
                return@launch
            }

            isLoading.value = true

            val hashed = HashUtil.sha256(password)
            val user = userDao.login(email, hashed)

            isLoading.value = false

            if (user != null) {
                loginState.value = "success"
                isLoggedIn.value = true
                loggedInUser.value = user
            } else {
                loginState.value = "Invalid email or password"
            }
        }
    }

    fun logout() {
        Log.d("UserViewModel", "Logging out. User was ${loggedInUser.value?.email}")
        loginState.value = null
        isLoggedIn.value = false
        loggedInUser.value = null
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            if (!ValidationUtil.isValidEmail(email)) {
                _registerState.value = "Invalid email format"
                return@launch
            }

            if (!ValidationUtil.isValidPassword(password)) {
                _registerState.value = "Password must be at least 8 characters and contain a number"
                return@launch
            }

            val result = repo.registerUser(fullName, email, password)
            _registerState.value = if (result > 0) "success" else "Failed to register"
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            repo.updateUser(updatedUser)
            loggedInUser.value = updatedUser 
        }
    }
}

package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.repository.UserRepository
import com.example.evite.utils.HashUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val repo = UserRepository(userDao)

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState = MutableStateFlow<String?>(null)
    val isLoading = MutableStateFlow(false)

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            loginState.value = null   // reset state properly

            val hashedPassword = HashUtil.sha256(password)
            val user = userDao.login(email, hashedPassword)

            isLoading.value = false

            loginState.value = if (user != null) {
                "success"
            } else {
                "Invalid email or password"
            }
        }
    }


    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repo.registerUser(fullName, email, password)
            _registerState.value = if (result > 0) "success" else "Failed to register"
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}

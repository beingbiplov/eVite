package com.example.evite.data.repository

import com.example.evite.data.local.dao.UserDao
import com.example.evite.data.local.entities.User
import com.example.evite.utils.HashUtil

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(fullName: String, email: String, password: String): Long {
        val hashedPassword = HashUtil.sha256(password)

        val user = User(
            fullName = fullName,
            email = email,
            password = hashedPassword
        )

        return userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        val hashedInput = HashUtil.sha256(password)
        return if (user != null && user.password == hashedInput) user else null
    }
}

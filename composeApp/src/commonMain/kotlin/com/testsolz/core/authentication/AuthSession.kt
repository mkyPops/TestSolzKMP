package com.testsolz.core.authentication

import com.testsolz.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthSession {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun update(token: String, user: User) {
        _token.value = token
        _currentUser.value = user
    }

    fun updateUser(user: User) {
        _currentUser.value = user
    }

    fun clear() {
        _token.value = null
        _currentUser.value = null
    }
}

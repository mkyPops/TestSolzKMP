package com.testsolz.core.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Authentication View Model
 * Handles login logic and authentication state
 */
class AuthenticationViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()
    
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }
    
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }
    
    // MARK: - Login
    fun login() {
        viewModelScope.launch {
            // Validate inputs
            if (email.value.isEmpty()) {
                _errorMessage.value = "Please enter your email"
                return@launch
            }
            
            if (password.value.isEmpty()) {
                _errorMessage.value = "Please enter your password"
                return@launch
            }
            
            // Show loading
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = apiClient.login(
                    email = email.value.trim(),
                    password = password.value
                )

                _authToken.value = response.token
                _currentUser.value = response.user
                AuthSession.update(token = response.token, user = response.user)
                _isAuthenticated.value = true
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Login failed"
                _isAuthenticated.value = false
            }
            
            _isLoading.value = false
        }
    }
    
    // MARK: - Logout
    fun logout() {
        _currentUser.value = null
        _authToken.value = null
        AuthSession.clear()
        _isAuthenticated.value = false
        _email.value = ""
        _password.value = ""
    }
    
    // MARK: - Clear Error
    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}

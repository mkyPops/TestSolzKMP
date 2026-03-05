package com.testsolz.core.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.domain.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Authentication View Model
 * Handles login logic and authentication state
 * In production, this would connect to your backend API
 */
class AuthenticationViewModel : ViewModel() {
    
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
            
            // Simulate API call (replace with real API in production)
            delay(1500) // 1.5 seconds
            
            // Mock authentication logic
            // In production: Call your backend API here
            when {
                email.value.lowercase() == "admin@testsolz.com" && password.value == "admin123" -> {
                    _currentUser.value = User.mockAdmin
                    _isAuthenticated.value = true
                }
                email.value.lowercase().contains("@testsolz.com") && password.value == "test123" -> {
                    _currentUser.value = User.mock
                    _isAuthenticated.value = true
                }
                else -> {
                    _errorMessage.value = "Invalid email or password"
                }
            }
            
            _isLoading.value = false
        }
    }
    
    // MARK: - Logout
    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _email.value = ""
        _password.value = ""
    }
    
    // MARK: - Clear Error
    fun clearError() {
        _errorMessage.value = null
    }
}

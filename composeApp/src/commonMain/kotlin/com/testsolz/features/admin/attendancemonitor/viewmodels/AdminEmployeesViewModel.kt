package com.testsolz.features.admin.attendancemonitor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminEmployeesViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()

    private val _employees = MutableStateFlow<List<User>>(emptyList())
    val employees: StateFlow<List<User>> = _employees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _employees.value = apiClient.employees().items
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load employees"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createEmployee(
        name: String,
        email: String,
        department: String,
        password: String,
        cardUid: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                apiClient.createEmployee(
                    name = name.trim(),
                    email = email.trim(),
                    department = department.trim(),
                    password = password,
                    cardUid = cardUid?.trim()?.takeIf { it.isNotBlank() }
                )
                _employees.value = apiClient.employees().items
                onSuccess()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to create employee"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateEmployee(
        employee: User,
        name: String,
        department: String,
        password: String?,
        cardUid: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                apiClient.updateEmployee(
                    id = employee.id,
                    name = name.trim(),
                    department = department.trim(),
                    password = password,
                    cardUid = cardUid?.trim()?.takeIf { it.isNotBlank() }
                )
                _employees.value = apiClient.employees().items
                onSuccess()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to update employee"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteEmployee(employee: User) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                if (apiClient.deleteEmployee(employee.id)) {
                    _employees.value = _employees.value.filterNot { it.id == employee.id }
                }
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to delete employee"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}

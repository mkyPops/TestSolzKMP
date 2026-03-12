package com.testsolz.features.admin.attendancemonitor.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.network.ApiRepository
import com.testsolz.network.models.Employee
import com.testsolz.network.models.CreateEmployeeRequest
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {
    private val api = ApiRepository()
    private var token = ""

    val employees = mutableStateOf<List<Employee>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val isLoggedIn = mutableStateOf(false)
    val employeeAdded = mutableStateOf(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            employees.value = emptyList()
            error.value = null
            try {
                println("AUTH: Attempting login with: $email")
                val response = api.login(email, password)
                token = response.token
                isLoggedIn.value = true
                println("AUTH: Login success, loading employees...")
                loadEmployees()
            } catch (e: Exception) {
                println("AUTH: Login failed: ${e.message}")
                error.value = "Login failed: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadEmployees() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = api.getEmployees(token)
                employees.value = response.items
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createEmployee(name: String, email: String, department: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                api.createEmployee(
                    token = token,
                    request = CreateEmployeeRequest(
                        name = name,
                        email = email,
                        department = department,
                        password = password
                    )
                )
                employeeAdded.value = true
                loadEmployees()
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun resetEmployeeAdded() {
        employeeAdded.value = false
    }
}

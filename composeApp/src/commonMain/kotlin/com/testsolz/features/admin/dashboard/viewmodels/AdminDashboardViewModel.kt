package com.testsolz.features.admin.dashboard.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Employee Info
 * Lightweight model for dashboard stat detail lists
 */
data class EmployeeInfo(
    val id: String,
    val name: String,
    val role: String,
    val department: String,
    val status: String,       // "Present", "On Leave", "Late", "Absent"
    val checkInTime: String?  // e.g. "9:05 AM", null if absent/on leave
)

/**
 * Stat Category
 * Represents which stat card was tapped
 */
enum class StatCategory(val title: String) {
    PRESENT_TODAY("Present Today"),
    ON_LEAVE("On Leave"),
    LATE_TODAY("Late Today"),
    PENDING_REQUESTS("Pending Requests")
}

/**
 * Pending Request Info
 * Lightweight model for pending request employees shown from dashboard
 */
data class PendingRequestInfo(
    val id: String,
    val employeeName: String,
    val department: String,
    val requestType: String,
    val reason: String,
    val date: String
)

/**
 * Admin Dashboard ViewModel
 * Manages dashboard metrics and stats
 */
class AdminDashboardViewModel : ViewModel() {

    private val _metrics = MutableStateFlow(DashboardMetrics())
    val metrics: StateFlow<DashboardMetrics> = _metrics.asStateFlow()

    private val _selectedCategory = MutableStateFlow<StatCategory?>(null)
    val selectedCategory: StateFlow<StatCategory?> = _selectedCategory.asStateFlow()

    // All mock employees
    private val _allEmployees = MutableStateFlow<List<EmployeeInfo>>(emptyList())
    val allEmployees: StateFlow<List<EmployeeInfo>> = _allEmployees.asStateFlow()

    private val _pendingRequestInfos = MutableStateFlow<List<PendingRequestInfo>>(emptyList())
    val pendingRequestInfos: StateFlow<List<PendingRequestInfo>> = _pendingRequestInfos.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val employees = listOf(
            EmployeeInfo("1", "Mashaal Khan", "Software Engineer", "Engineering", "Present", "8:55 AM"),
            EmployeeInfo("2", "Sarah Wilson", "UI/UX Designer", "Design", "On Leave", null),
            EmployeeInfo("3", "Mike Johnson", "Marketing Lead", "Marketing", "Late", "10:15 AM"),
            EmployeeInfo("4", "Jane Smith", "Senior Developer", "Engineering", "Present", "8:48 AM"),
            EmployeeInfo("5", "Ahmed Ali", "QA Engineer", "Quality Assurance", "Present", "9:00 AM"),
            EmployeeInfo("6", "Emily Brown", "HR Coordinator", "Human Resources", "Present", "8:50 AM"),
            EmployeeInfo("7", "David Lee", "DevOps Engineer", "Engineering", "Present", "8:45 AM"),
            EmployeeInfo("8", "Fatima Zahra", "Product Manager", "Product", "On Leave", null),
            EmployeeInfo("9", "Chris Taylor", "Backend Developer", "Engineering", "Present", "9:02 AM"),
            EmployeeInfo("10", "Aisha Malik", "Graphic Designer", "Design", "Present", "8:58 AM"),
            EmployeeInfo("11", "James Wilson", "Sales Executive", "Sales", "Late", "10:30 AM"),
            EmployeeInfo("12", "Nadia Hussain", "Content Writer", "Marketing", "Present", "8:52 AM"),
            EmployeeInfo("13", "Robert Chen", "Data Analyst", "Analytics", "Present", "9:01 AM"),
            EmployeeInfo("14", "Sana Iqbal", "Frontend Developer", "Engineering", "Present", "8:47 AM"),
            EmployeeInfo("15", "Tom Anderson", "Project Manager", "Product", "Present", "8:55 AM"),
            EmployeeInfo("16", "Hira Shah", "Business Analyst", "Analytics", "On Leave", null),
            EmployeeInfo("17", "Daniel Garcia", "iOS Developer", "Engineering", "Present", "8:59 AM"),
            EmployeeInfo("18", "Zara Khan", "Social Media Manager", "Marketing", "Present", "9:03 AM"),
            EmployeeInfo("19", "William Brown", "Network Engineer", "IT", "Present", "8:46 AM"),
            EmployeeInfo("20", "Amna Tariq", "Accountant", "Finance", "Present", "8:50 AM"),
            EmployeeInfo("21", "Kevin White", "Security Analyst", "IT", "Late", "10:45 AM"),
            EmployeeInfo("22", "Rabia Noor", "Test Engineer", "Quality Assurance", "Present", "8:53 AM"),
            EmployeeInfo("23", "Steven Clark", "Android Developer", "Engineering", "Present", "8:57 AM"),
            EmployeeInfo("24", "Maria Lopez", "UX Researcher", "Design", "Present", "9:00 AM"),
            EmployeeInfo("25", "Hassan Raza", "Technical Writer", "Engineering", "Present", "8:49 AM"),
            EmployeeInfo("26", "Jessica Moore", "Office Manager", "Operations", "Present", "8:44 AM"),
            EmployeeInfo("27", "Ali Hassan", "Database Admin", "IT", "Present", "8:56 AM"),
            EmployeeInfo("28", "Laura Martin", "Recruitment Lead", "Human Resources", "On Leave", null),
            EmployeeInfo("29", "Omar Farooq", "Full Stack Developer", "Engineering", "Present", "8:51 AM"),
            EmployeeInfo("30", "Sophie Turner", "Brand Manager", "Marketing", "Present", "9:04 AM"),
            EmployeeInfo("31", "Ryan Phillips", "Cloud Architect", "Engineering", "Present", "8:48 AM"),
            EmployeeInfo("32", "Ayesha Siddiqui", "Legal Advisor", "Legal", "Present", "8:55 AM"),
            EmployeeInfo("33", "Nathan Brooks", "Support Lead", "Customer Support", "Present", "8:52 AM"),
            EmployeeInfo("34", "Priya Sharma", "ML Engineer", "Engineering", "Present", "9:01 AM"),
            EmployeeInfo("35", "Mark Robinson", "Finance Manager", "Finance", "Present", "8:47 AM"),
            EmployeeInfo("36", "Khadija Begum", "Operations Analyst", "Operations", "Present", "8:58 AM"),
            EmployeeInfo("37", "George Harris", "System Admin", "IT", "Present", "8:50 AM"),
            EmployeeInfo("38", "Samira Yousuf", "Training Coordinator", "Human Resources", "Present", "8:54 AM"),
            EmployeeInfo("39", "Peter Jackson", "Sales Manager", "Sales", "Present", "8:46 AM"),
            EmployeeInfo("40", "Maryam Bibi", "Compliance Officer", "Legal", "Present", "8:53 AM"),
            EmployeeInfo("41", "Brian Adams", "Creative Director", "Design", "Present", "8:49 AM"),
            EmployeeInfo("42", "Usman Ghani", "DevSecOps Engineer", "Engineering", "Present", "9:00 AM"),
            EmployeeInfo("43", "Rachel Green", "PR Manager", "Marketing", "Present", "8:57 AM"),
            EmployeeInfo("44", "Bilal Ahmed", "IT Support", "IT", "Present", "8:51 AM"),
            EmployeeInfo("45", "Diana Prince", "Executive Assistant", "Operations", "Present", "8:55 AM")
        )

        _allEmployees.value = employees

        // Counts: 45 total, 38 present, 4 on leave, 3 late, 7 pending requests
        _metrics.value = DashboardMetrics(
            totalEmployees = employees.size,
            presentToday = employees.count { it.status == "Present" },
            onLeave = employees.count { it.status == "On Leave" },
            lateToday = employees.count { it.status == "Late" },
            pendingRequests = 7
        )

        _pendingRequestInfos.value = listOf(
            PendingRequestInfo("1", "Sarah Wilson", "Design", "Leave - Sick", "Flu", "Mar 6 – Mar 8, 2026"),
            PendingRequestInfo("2", "Mike Johnson", "Marketing", "Late Arrival", "Car trouble", "Mar 6, 2026"),
            PendingRequestInfo("3", "Fatima Zahra", "Product", "Leave - Vacation", "Family vacation", "Mar 10 – Mar 14, 2026"),
            PendingRequestInfo("4", "Hira Shah", "Analytics", "Leave - Personal", "Personal matters", "Mar 9, 2026"),
            PendingRequestInfo("5", "Laura Martin", "Human Resources", "Leave - Sick", "Medical appointment", "Mar 7, 2026"),
            PendingRequestInfo("6", "James Wilson", "Sales", "Late Arrival", "Train delay", "Mar 6, 2026"),
            PendingRequestInfo("7", "Kevin White", "IT", "Late Arrival", "Traffic congestion", "Mar 6, 2026")
        )
    }

    fun getEmployeesForCategory(category: StatCategory): List<EmployeeInfo> {
        return when (category) {
            StatCategory.PRESENT_TODAY -> _allEmployees.value.filter { it.status == "Present" }
            StatCategory.ON_LEAVE -> _allEmployees.value.filter { it.status == "On Leave" }
            StatCategory.LATE_TODAY -> _allEmployees.value.filter { it.status == "Late" }
            StatCategory.PENDING_REQUESTS -> emptyList() // Handled separately
        }
    }

    fun selectCategory(category: StatCategory) {
        _selectedCategory.value = category
    }

    fun clearCategory() {
        _selectedCategory.value = null
    }
}

data class DashboardMetrics(
    val totalEmployees: Int = 0,
    val presentToday: Int = 0,
    val onLeave: Int = 0,
    val lateToday: Int = 0,
    val pendingRequests: Int = 0
)

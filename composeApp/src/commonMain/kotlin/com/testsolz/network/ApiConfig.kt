package com.testsolz.network

object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:3000/v1"

    // Auth
    const val LOGIN     = "$BASE_URL/auth/login"
    const val ME        = "$BASE_URL/auth/me"

    // Admin - Employees
    const val EMPLOYEES = "$BASE_URL/admin/employees"

    // Attendance
    const val ATTENDANCE_ME     = "$BASE_URL/attendance/me"
    const val ATTENDANCE_STATUS = "$BASE_URL/attendance/admin/status"

    // Requests (Leave & Late)
    const val REQUESTS       = "$BASE_URL/requests"
    const val REQUESTS_ME    = "$BASE_URL/requests/me"
    const val REQUESTS_ADMIN = "$BASE_URL/requests/admin"
}

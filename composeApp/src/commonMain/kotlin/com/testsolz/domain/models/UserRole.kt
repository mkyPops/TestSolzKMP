package com.testsolz.domain.models

import kotlinx.serialization.Serializable

/**
 * User Role
 * Defines the two types of users in the system
 * Employee: Can check in/out, view own attendance
 * Admin: Can view all attendance, manage users, see dashboard
 */
@Serializable
enum class UserRole(val value: String) {
    EMPLOYEE("employee"),
    ADMIN("admin");

    val displayName: String
        get() = when (this) {
            EMPLOYEE -> "Employee"
            ADMIN -> "HR/Admin"
        }
}

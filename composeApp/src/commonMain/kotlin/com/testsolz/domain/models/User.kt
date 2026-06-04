package com.testsolz.domain.models

import kotlinx.serialization.Serializable

/**
 * User Model
 * Represents a user (employee or admin) in the system
 */
@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val department: String? = null,
    val cardUid: String? = null,
    val status: String? = null,
    val profileImageURL: String? = null
) {
    companion object {
        // For preview/testing
        val mock = User(
            id = "1",
            email = "mashaal@testsolz.com",
            name = "Mashaal Khan",
            role = UserRole.EMPLOYEE,
            department = "Engineering",
            profileImageURL = null
        )

        val mockAdmin = User(
            id = "2",
            email = "admin@testsolz.com",
            name = "Admin User",
            role = UserRole.ADMIN,
            department = "HR",
            profileImageURL = null
        )
    }
}

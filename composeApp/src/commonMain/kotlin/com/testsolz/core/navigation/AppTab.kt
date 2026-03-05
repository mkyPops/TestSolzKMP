package com.testsolz.core.navigation

/**
 * App Tab
 * Defines available tabs for employee and admin
 */
enum class AppTab(
    val title: String,
    val icon: String,
    val iconFilled: String
) {
    // Employee tabs
    HOME("Home", "house", "house_fill"),
    HISTORY("History", "calendar", "calendar"),
    REQUESTS("Requests", "doc_text", "doc_text_fill"),
    PROFILE("Profile", "person", "person_fill"),
    
    // Admin tabs
    DASHBOARD("Dashboard", "chart_bar", "chart_bar_fill"),
    EMPLOYEES("Employees", "person_2", "person_2_fill"),
    ADMIN_REQUESTS("Requests", "doc_text", "doc_text_fill");
    
    companion object {
        // Employee tabs
        val employeeTabs = listOf(HOME, HISTORY, REQUESTS, PROFILE)
        
        // Admin tabs
        val adminTabs = listOf(DASHBOARD, EMPLOYEES, ADMIN_REQUESTS)
    }
}

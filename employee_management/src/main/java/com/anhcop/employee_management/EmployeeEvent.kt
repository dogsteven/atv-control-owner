package com.anhcop.employee_management

sealed interface EmployeeEvent {
    data class EmployeeAdded(val employee: Employee): EmployeeEvent
    data class EmployeeDeleted(val id: String): EmployeeEvent
    data class EmployeeModified(val employee: Employee): EmployeeEvent
}
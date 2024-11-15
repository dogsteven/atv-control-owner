package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list

import com.anhcop.employee_management.Employee

data class EmployeeEntry(
    val id: String,
    val firstname: String,
    val lastname: String,
    val deviceIdentifier: String
) {
    constructor(employee: Employee): this(employee.id, employee.firstname, employee.lastname, employee.deviceIdentifier)
}
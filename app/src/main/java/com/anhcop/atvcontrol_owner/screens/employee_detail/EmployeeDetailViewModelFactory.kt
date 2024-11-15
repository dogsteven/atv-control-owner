package com.anhcop.atvcontrol_owner.screens.employee_detail

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface EmployeeDetailViewModelFactory {
    fun create(@Assisted("id") id: String): EmployeeDetailViewModel
}
package com.anhcop.atvcontrol_owner.screens.employee_editor

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface EmployeeEditorViewModelFactory {
    fun create(@Assisted("id") id: String?): EmployeeEditorViewModel
}
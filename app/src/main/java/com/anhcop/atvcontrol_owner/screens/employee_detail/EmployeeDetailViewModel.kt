package com.anhcop.atvcontrol_owner.screens.employee_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.employee_management.Employee
import com.anhcop.employee_management.EmployeeEvent
import com.anhcop.employee_management.EmployeeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = EmployeeDetailViewModelFactory::class)
class EmployeeDetailViewModel @AssistedInject constructor(
    @Assisted("id") val id: String,
    private val employeeRepository: EmployeeRepository
): ViewModel() {
    private lateinit var listenToEmployeeEventsJob: Job

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _firstname = mutableStateOf("")
    val firstname by _firstname

    private val _lastname = mutableStateOf("")
    val lastname by _lastname

    private val _deviceIdentifier = mutableStateOf("")
    val deviceIdentifier by _deviceIdentifier

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val employee = employeeRepository.getEmployeeById(id)

            if (employee == null) {
                _status.value = Status.Failed
            } else {
                updateFromEmployee(employee)

                listenToEmployeeEventsJob = employeeRepository.events.onEach { event ->
                    if (event is EmployeeEvent.EmployeeModified) {
                        if (event.employee.id == id) {
                            updateFromEmployee(event.employee)
                        }
                    }
                }.launchIn(viewModelScope)

                _status.value = Status.Successful
            }
        }
    }

    private fun updateFromEmployee(employee: Employee) {
        _firstname.value = employee.firstname
        _lastname.value = employee.lastname
        _deviceIdentifier.value = employee.deviceIdentifier
    }

    fun deleteEmployee(
        onDeleted: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                employeeRepository.delete(id)
                onDeleted()
            } catch (_: Throwable) {
                onError()
            }
        }
    }
}
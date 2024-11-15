package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.employee_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.employee_management.EmployeeEvent
import com.anhcop.employee_management.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EmployeeListViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
): ViewModel() {
    private val _employeeEntries = mutableStateListOf<EmployeeEntry>()
    val employeeEntries: List<EmployeeEntry> = _employeeEntries

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _isLoading = mutableStateOf(false)
    val isLoading by _isLoading

    private var listenToEmployeeEventsJob: Job? = null

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        if (_isLoading.value) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                listenToEmployeeEventsJob?.cancel()
                _employeeEntries.clear()

                val employeeEntries = withContext(Dispatchers.Default) {
                    employeeRepository.getAllEmployees().map(::EmployeeEntry).sortedBy { it.firstname }
                }

                _employeeEntries.addAll(employeeEntries)

                listenToEmployeeEventsJob = employeeRepository.events.onEach { event ->
                    when (event) {
                        is EmployeeEvent.EmployeeAdded -> {
                            val index = _employeeEntries.indexOfFirst { it.firstname > event.employee.firstname }

                            if (index == -1) {
                                _employeeEntries.add(EmployeeEntry(event.employee))
                            } else {
                                _employeeEntries.add(index, EmployeeEntry(event.employee))
                            }
                        }

                        is EmployeeEvent.EmployeeDeleted -> {
                            val index = _employeeEntries.indexOfFirst { it.id == event.id }

                            if (index != -1) {
                                _employeeEntries.removeAt(index)
                            }
                        }

                        is EmployeeEvent.EmployeeModified -> {
                            val index = _employeeEntries.indexOfFirst { it.id == event.employee.id }

                            if (index != -1) {
                                val employeeEntry = _employeeEntries[index]

                                if (event.employee.firstname != employeeEntry.firstname) {
                                    _employeeEntries.removeAt(index)

                                    val newIndex = _employeeEntries.indexOfFirst { it.firstname > event.employee.firstname }

                                    if (newIndex == -1) {
                                        _employeeEntries.add(EmployeeEntry(event.employee))
                                    } else {
                                        _employeeEntries.add(newIndex, EmployeeEntry(event.employee))
                                    }
                                } else {
                                    _employeeEntries[index] = EmployeeEntry(event.employee)
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)

                _status.value = Status.Successful
            } catch (_: Throwable) {
                _status.value = Status.Failed
            } finally {
                _isLoading.value = false
            }
        }
    }
}
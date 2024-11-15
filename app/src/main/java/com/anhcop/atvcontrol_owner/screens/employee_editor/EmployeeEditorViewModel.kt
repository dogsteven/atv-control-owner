package com.anhcop.atvcontrol_owner.screens.employee_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.employee_management.Employee
import com.anhcop.employee_management.EmployeeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = EmployeeEditorViewModelFactory::class)
class EmployeeEditorViewModel @AssistedInject constructor(
    @Assisted("id") id: String?,
    private val employeeRepository: EmployeeRepository
): ViewModel() {
    companion object {
        private val DeviceIdentifierRegex = Regex("[0-9a-f]+")
    }

    private lateinit var employee: Employee

    private val _editorMode = mutableStateOf(EditorMode.Add)
    val editorMode by _editorMode

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _firstname = mutableStateOf("")
    var firstname by _firstname

    private val _lastname = mutableStateOf("")
    var lastname by _lastname

    private val _deviceIdentifier = mutableStateOf("")
    var deviceIdentifier by _deviceIdentifier

    private val _isFirstnameValid = mutableStateOf(true)
    val isFirstnameValid by _isFirstnameValid

    private val _isLastnameValid = mutableStateOf(true)
    val isLastnameValid by _isLastnameValid

    private val _isDeviceIdentifierValid = mutableStateOf(true)
    val isDeviceIdentifierValid by _isDeviceIdentifierValid

    private val _isSubmitting = mutableStateOf(false)
    val isSubmitting by _isSubmitting

    private val isAllInputsValid: Boolean
        get() = _isFirstnameValid.value && _isLastnameValid.value && _isDeviceIdentifierValid.value

    init {
        initialize(id)
    }

    private fun initialize(id: String?) {
        viewModelScope.launch {
            if (id == null) {
                employee = Employee.create("", "", "")

                _editorMode.value = EditorMode.Add
                _status.value = Status.Successful
            } else {
                val existingEmployee = employeeRepository.getEmployeeById(id)

                if (existingEmployee == null) {
                    _status.value = Status.Failed
                } else {
                    employee = existingEmployee
                    updateFromEmployee()

                    _editorMode.value = EditorMode.Update
                    _status.value = Status.Successful
                }
            }
        }
    }

    private fun updateFromEmployee() {
        _firstname.value = employee.firstname
        _lastname.value = employee.lastname
        _deviceIdentifier.value = employee.deviceIdentifier
    }

    fun reverseChanges() {
        if (_editorMode.value == EditorMode.Add) {
            return
        }

        _isFirstnameValid.value = true
        _isLastnameValid.value = true
        _isDeviceIdentifierValid.value = true
        updateFromEmployee()
    }

    private fun validateFirstname() {
        _isFirstnameValid.value = _firstname.value.isNotBlank()
    }

    private fun validateLastname() {
        _isLastnameValid.value = _lastname.value.isNotBlank()
    }

    private fun validateDeviceIdentifier() {
        _isDeviceIdentifierValid.value = DeviceIdentifierRegex.matches(_deviceIdentifier.value)
    }

    fun submit(
        onAdded: () -> Unit,
        onUpdated: () -> Unit,
        onError: () -> Unit
    ) {
        if (_isSubmitting.value) {
            return
        }

        _isSubmitting.value = true

        viewModelScope.launch {
            validateFirstname()
            validateLastname()
            validateDeviceIdentifier()

            if (!isAllInputsValid) {
                _isSubmitting.value = false
                return@launch
            }

            employee.firstname = _firstname.value
            employee.lastname = _lastname.value
            employee.deviceIdentifier = _deviceIdentifier.value

            try {
                employeeRepository.save(employee)

                if (_editorMode.value == EditorMode.Add) {
                    onAdded()
                    _editorMode.value = EditorMode.Update
                } else {
                    onUpdated()
                }
            } catch (_: Throwable) {
                onError()
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
package com.anhcop.atvcontrol_owner.screens.vehicle_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.vehicle_management.Vehicle
import com.anhcop.vehicle_management.VehicleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = VehicleEditorViewModelFactory::class)
class VehicleEditorViewModel @AssistedInject constructor(
    @Assisted("id") id: String?,
    private val vehicleRepository: VehicleRepository
): ViewModel() {
    companion object {
        private val MacAddressRegex = Regex("[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}")
        private val LocalIPRegex = Regex("[0-9]{1,3}(.[0-9]{1,3}){3}")
    }

    private lateinit var vehicle: Vehicle

    private val _editorMode = mutableStateOf(EditorMode.Add)
    val editorMode by _editorMode

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _name = mutableStateOf("")
    var name by _name

    private val _macAddress = mutableStateOf("")
    var macAddress by _macAddress

    private val _localIP = mutableStateOf("")
    var localIP by _localIP

    private val _price = mutableStateOf("")
    var price by _price

    private val _isNameValid = mutableStateOf(true)
    val isNameValid by _isNameValid

    private val _isMacAddressValid = mutableStateOf(true)
    val isMacAddressValid by _isMacAddressValid

    private val _isLocalIPValid = mutableStateOf(true)
    val isLocalIPValid by _isLocalIPValid

    private val _isPriceValid = mutableStateOf(true)
    val isPriceValid by _isPriceValid

    private val _isSubmitting = mutableStateOf(false)
    val isSubmitting by _isSubmitting

    private val isInputsValid: Boolean
        get() {
            return _isNameValid.value && _isMacAddressValid.value && _isLocalIPValid.value && _isPriceValid.value
        }

    init {
        initialize(id)
    }

    private fun initialize(id: String?) {
        viewModelScope.launch {
            if (id == null) {
                vehicle = Vehicle.create("", "", "", 0)

                _editorMode.value = EditorMode.Add
                _status.value = Status.Successful
            } else {
                val existingVehicle = vehicleRepository.getVehicleById(id)

                if (existingVehicle == null) {
                    _status.value = Status.Failed
                } else {
                    vehicle = existingVehicle
                    updateFromVehicle()

                    _editorMode.value = EditorMode.Update
                    _status.value = Status.Successful
                }
            }
        }
    }

    private fun updateFromVehicle() {
        _name.value = vehicle.name
        _macAddress.value = vehicle.macAddress
        _localIP.value = vehicle.localIP
        _price.value = vehicle.price.toString()
    }

    fun reverseChanges() {
        if (_editorMode.value == EditorMode.Add) {
            return
        }

        _isNameValid.value = true
        _isMacAddressValid.value = true
        _isLocalIPValid.value = true
        _isPriceValid.value = true
        updateFromVehicle()
    }

    private fun validateName() {
        _isNameValid.value = _name.value.isNotBlank()
    }

    private fun validateMacAddress() {
        _isMacAddressValid.value = MacAddressRegex.matches(_macAddress.value)
    }

    private fun validateLocalIP() {
        _isLocalIPValid.value = LocalIPRegex.matches(_localIP.value)
    }

    private fun validatePrice() {
        _isPriceValid.value = _price.value.toLongOrNull()?.run { this > 0 } ?: false
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
            validateName()
            validateMacAddress()
            validateLocalIP()
            validatePrice()

            if (!isInputsValid) {
                _isSubmitting.value = false
                return@launch
            }

            vehicle.name = _name.value
            vehicle.macAddress = _macAddress.value
            vehicle.localIP = _localIP.value
            vehicle.price = _price.value.toLong()

            try {
                vehicleRepository.save(vehicle)

                delay(200)
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
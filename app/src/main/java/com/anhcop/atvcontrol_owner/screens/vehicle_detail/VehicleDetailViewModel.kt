package com.anhcop.atvcontrol_owner.screens.vehicle_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.vehicle_management.Vehicle
import com.anhcop.vehicle_management.VehicleEvent
import com.anhcop.vehicle_management.VehicleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = VehicleDetailViewModelFactory::class)
class VehicleDetailViewModel @AssistedInject constructor(
    @Assisted("id") val id: String,
    private val vehicleRepository: VehicleRepository
): ViewModel() {
    private lateinit var listenToVehicleEventsJob: Job

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _name = mutableStateOf("")
    val name by _name

    private val _macAddress = mutableStateOf("")
    val macAddress by _macAddress

    private val _localIP = mutableStateOf("")
    val localIP by _localIP

    private val _price = mutableLongStateOf(0L)
    val price by _price

    private val _isDeleting = mutableStateOf(false)
    val isDeleting by _isDeleting

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(id)

            if (vehicle == null) {
                _status.value = Status.Failed
            } else {
                updateFromVehicle(vehicle)

                listenToVehicleEventsJob = vehicleRepository.events.onEach { event ->
                    if (event is VehicleEvent.VehicleModified) {
                        if (event.vehicle.id == id) {
                            updateFromVehicle(event.vehicle)
                        }
                    }
                }.launchIn(viewModelScope)

                _status.value = Status.Successful
            }
        }
    }

    private fun updateFromVehicle(vehicle: Vehicle) {
        _name.value = vehicle.name
        _macAddress.value = vehicle.macAddress
        _localIP.value = vehicle.localIP
        _price.longValue = vehicle.price
    }

    fun deleteVehicle(
        onDeleted: () -> Unit,
        onError: () -> Unit
    ) {
        if (_isDeleting.value) {
            return
        }

        _isDeleting.value = true

        viewModelScope.launch {
            try {
                vehicleRepository.delete(id)
                onDeleted()
            } catch (_: Throwable) {
                onError()
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
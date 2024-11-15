package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhcop.vehicle_management.VehicleEvent
import com.anhcop.vehicle_management.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val vehicleControlService: VehicleControlService
): ViewModel() {
    private val _vehicleEntries = mutableStateListOf<VehicleEntry>()
    val vehicleEntries: List<VehicleEntry> = _vehicleEntries

    private val _status = mutableStateOf(Status.Uninitialized)
    val status by _status

    private val _isLoading = mutableStateOf(false)
    val isLoading by _isLoading

    private var listenToVehicleEventsJob: Job? = null

    init {
        loadVehicles()
    }

    fun loadVehicles() {
        if (_isLoading.value) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                listenToVehicleEventsJob?.cancel()
                _vehicleEntries.clear()

                val vehicleEntries = withContext(Dispatchers.Default) {
                    vehicleRepository.getAllVehicles().map(::VehicleEntry).sortedBy { it.name }
                }

                _vehicleEntries.addAll(vehicleEntries)

                listenToVehicleEventsJob = vehicleRepository.events.onEach { event ->
                    when (event) {
                        is VehicleEvent.VehicleAdded -> {
                            val index = _vehicleEntries.indexOfFirst { it.name > event.vehicle.name }

                            if (index == -1) {
                                _vehicleEntries.add(VehicleEntry(event.vehicle))
                            } else {
                                _vehicleEntries.add(index, VehicleEntry(event.vehicle))
                            }
                        }

                        is VehicleEvent.VehicleDeleted -> {
                            val index = _vehicleEntries.indexOfFirst { it.id == event.id }

                            if (index != -1) {
                                _vehicleEntries.removeAt(index)
                            }
                        }

                        is VehicleEvent.VehicleModified -> {
                            val index = _vehicleEntries.indexOfFirst { it.id == event.vehicle.id }

                            if (index != -1) {
                                val vehicleEntry = _vehicleEntries[index]

                                if (event.vehicle.name != vehicleEntry.name) {
                                    _vehicleEntries.removeAt(index)

                                    val newIndex = _vehicleEntries.indexOfFirst { it.name > event.vehicle.name }

                                    if (newIndex == -1) {
                                        _vehicleEntries.add(VehicleEntry(event.vehicle))
                                    } else {
                                        _vehicleEntries.add(newIndex, VehicleEntry(event.vehicle))
                                    }
                                } else {
                                    _vehicleEntries[index] = VehicleEntry(event.vehicle)
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

    fun toggleVehiclePower(index: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (index < 0 || index >= _vehicleEntries.size) {
            return
        }

        _vehicleEntries[index] = _vehicleEntries[index].copy(isFetching = true)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    vehicleControlService.toggleVehiclePower(_vehicleEntries[index].localIP)
                    onSuccess()
                } catch (_: Throwable) {
                    onFailure()
                } finally {
                    _vehicleEntries[index] = _vehicleEntries[index].copy(isFetching = false)
                }
            }
        }
    }
}
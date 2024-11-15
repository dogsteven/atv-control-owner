package com.anhcop.vehicle_management

sealed interface VehicleEvent {
    data class VehicleAdded(val vehicle: Vehicle): VehicleEvent
    data class VehicleDeleted(val id: String): VehicleEvent
    data class VehicleModified(val vehicle: Vehicle): VehicleEvent
}
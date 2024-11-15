package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list

import androidx.compose.runtime.Immutable
import com.anhcop.vehicle_management.Vehicle

@Immutable
data class VehicleEntry(
    val id: String,
    val name: String,
    val macAddress: String,
    val localIP: String,
    val price: Long,
    val isFetching: Boolean = false
) {
    constructor(vehicle: Vehicle): this(vehicle.id, vehicle.name, vehicle.macAddress, vehicle.localIP, vehicle.price)
}
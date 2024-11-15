package com.anhcop.atvcontrol_owner.screens.vehicle_editor

import com.anhcop.vehicle_management.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface VehicleEditorViewModelFactory {
    fun create(@Assisted("id") id: String?): VehicleEditorViewModel
}
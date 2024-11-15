package com.anhcop.atvcontrol_owner.screens.vehicle_editor

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface VehicleEditorViewModelFactory {
    fun create(@Assisted("id") id: String?): VehicleEditorViewModel
}
package com.anhcop.atvcontrol_owner.screens.vehicle_detail

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface VehicleDetailViewModelFactory {
    fun create(
        @Assisted("id") id: String
    ): VehicleDetailViewModel
}
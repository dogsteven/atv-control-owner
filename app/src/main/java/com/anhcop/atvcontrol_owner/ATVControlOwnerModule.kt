package com.anhcop.atvcontrol_owner

import com.anhcop.analytic.AnalyticService
import com.anhcop.atvcontrol_owner.utils.FirestoreFactory
import com.anhcop.configuration_management.ConfigurationService
import com.anhcop.employee_management.EmployeeRepository
import com.anhcop.vehicle_management.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ATVControlOwnerModule {
    @Provides
    @Singleton
    fun provideVehicleRepository(
        firestoreFactory: FirestoreFactory
    ) = VehicleRepository(
        firestoreFactory = firestoreFactory::create
    )

    @Provides
    @Singleton
    fun provideEmployeeRepository(
        firestoreFactory: FirestoreFactory
    ) = EmployeeRepository(
        firestoreFactory = firestoreFactory::create
    )

    @Provides
    @Singleton
    fun provideConfigurationService(
        firestoreFactory: FirestoreFactory
    ) = ConfigurationService(
        firestoreFactory = firestoreFactory::create
    )

    @Provides
    @Singleton
    fun provideAnalyticService(
        firestoreFactory: FirestoreFactory
    ) = AnalyticService(
        firestoreFactory = firestoreFactory::create
    )
}
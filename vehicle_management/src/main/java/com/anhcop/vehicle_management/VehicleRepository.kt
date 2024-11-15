package com.anhcop.vehicle_management

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await

class VehicleRepository(private val firestoreFactory: () -> FirebaseFirestore) {
    companion object {
        private fun DocumentSnapshot.toVehicle(): Vehicle? {
            val name = getString(Vehicle.NAME) ?: return null
            val macAddress = getString(Vehicle.MAC_ADDRESS) ?: return null
            val localIP = getString(Vehicle.LOCAL_IP) ?: return null
            val price = getLong(Vehicle.PRICE) ?: return null

            return Vehicle(id, name, macAddress, localIP, price, Vehicle.EntityState.Persisted)
        }
    }

    private val collection: CollectionReference
        get() = firestoreFactory().collection("vehicles")

    private val _events = MutableSharedFlow<VehicleEvent>()
    val events = _events.asSharedFlow()

    suspend fun getAllVehicles(): List<Vehicle> {
        return collection.get().await().mapNotNull { document ->
            document.toVehicle()
        }
    }

    suspend fun getVehicleById(id: String): Vehicle? {
        return try {
            collection.document(id).get().await().toVehicle()
        } catch (_: Throwable) {
            null
        }
    }

    suspend fun save(vehicle: Vehicle) {
        if (vehicle.entityState == Vehicle.EntityState.New) {
            collection.document(vehicle.id).set(mapOf(
                Vehicle.NAME to vehicle.name,
                Vehicle.MAC_ADDRESS to vehicle.macAddress,
                Vehicle.LOCAL_IP to vehicle.localIP,
                Vehicle.PRICE to vehicle.price
            ))

            vehicle.markAsPersisted()
            _events.emit(VehicleEvent.VehicleAdded(vehicle))
        } else if (vehicle.entityState == Vehicle.EntityState.Persisted && vehicle.isModified) {
            collection.document(vehicle.id).update(vehicle.changes)

            vehicle.applyChanges()
            _events.emit(VehicleEvent.VehicleModified(vehicle))
        }
    }

    suspend fun delete(id: String) {
        collection.document(id).delete()
        _events.emit(VehicleEvent.VehicleDeleted(id))
    }
}
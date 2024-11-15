package com.anhcop.employee_management

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await

class EmployeeRepository(private val firestoreFactory: () -> FirebaseFirestore) {
    companion object {
        private fun DocumentSnapshot.toEmployee(): Employee? {
            val id = id
            val firstname = getString(Employee.FIRSTNAME) ?: return null
            val lastname = getString(Employee.LASTNAME) ?: return null
            val deviceIdentifier = getString(Employee.DEVICE_IDENTIFIER) ?: return null

            return Employee(id, firstname, lastname, deviceIdentifier, Employee.EntityState.Persisted)
        }
    }

    private val collection: CollectionReference
        get() = firestoreFactory().collection("employees")

    private val _events = MutableSharedFlow<EmployeeEvent>()
    val events = _events.asSharedFlow()

    suspend fun getAllEmployees(): List<Employee> {
        return collection.get().await().mapNotNull { document -> document.toEmployee() }
    }

    suspend fun getEmployeeById(id: String): Employee? {
        return try {
            collection.document(id).get().await().toEmployee()
        } catch (_: Throwable) {
            null
        }
    }

    suspend fun save(employee: Employee) {
        if (employee.entityState == Employee.EntityState.New) {
            collection.document(employee.id).set(mapOf(
                Employee.FIRSTNAME to employee.firstname,
                Employee.LASTNAME to employee.lastname,
                Employee.DEVICE_IDENTIFIER to employee.deviceIdentifier
            ))

            employee.markAsPersisted()
            _events.emit(EmployeeEvent.EmployeeAdded(employee))
        } else if (employee.entityState == Employee.EntityState.Persisted) {
            collection.document(employee.id).update(employee.changes)

            employee.applyChanges()
            _events.emit(EmployeeEvent.EmployeeModified(employee))
        }
    }

    suspend fun delete(id: String) {
        collection.document(id).delete()
        _events.emit(EmployeeEvent.EmployeeDeleted(id))
    }
}
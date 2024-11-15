package com.anhcop.employee_management

import java.util.UUID

class Employee internal constructor(
    val id: String,
    firstname: String,
    lastname: String,
    deviceIdentifier: String,
    entityState: EntityState
) {
    internal enum class EntityState {
        New, Persisted
    }

    companion object {
        internal const val FIRSTNAME = "firstname"
        internal const val LASTNAME = "lastname"
        internal const val DEVICE_IDENTIFIER = "device_identifier"

        fun create(firstname: String, lastname: String, deviceIdentifier: String): Employee {
            val id = UUID.randomUUID().toString()
            return Employee(id, firstname, lastname, deviceIdentifier, EntityState.New)
        }
    }

    private var _entityState: EntityState = entityState
    internal val entityState: EntityState get() = _entityState

    private val _changes = mutableMapOf<String, Any>()
    internal val changes: Map<String, Any> = _changes

    private var _firstname: String = firstname
    var firstname: String
        get() = (_changes[FIRSTNAME] as? String) ?: _firstname
        set(value) {
            if (_entityState == EntityState.New) {
                _firstname = value
            } else {
                if (value == _firstname) {
                    _changes.remove(FIRSTNAME)
                } else {
                    _changes[FIRSTNAME] = value
                }
            }
        }

    private var _lastname: String = lastname
    var lastname: String
        get() = (_changes[LASTNAME] as? String) ?: _lastname
        set(value) {
            if (_entityState == EntityState.New) {
                _lastname = value
            } else {
                if (value == _lastname) {
                    _changes.remove(LASTNAME)
                } else {
                    _changes[LASTNAME] = value
                }
            }
        }

    private var _deviceIdentifier: String = deviceIdentifier
    var deviceIdentifier: String
        get() = (_changes[DEVICE_IDENTIFIER] as? String) ?: _deviceIdentifier
        set(value) {
            if (_entityState == EntityState.New) {
                _deviceIdentifier = value
            } else {
                if (value == _deviceIdentifier) {
                    _changes.remove(DEVICE_IDENTIFIER)
                } else {
                    _changes[DEVICE_IDENTIFIER] = value
                }
            }
        }

    internal fun applyChanges() {
        if (_entityState == EntityState.Persisted) {
            for ((key, value) in _changes) {
                when (key) {
                    FIRSTNAME -> _firstname = value as String
                    LASTNAME -> _lastname = value as String
                    DEVICE_IDENTIFIER -> _deviceIdentifier = value as String
                }
            }

            _changes.clear()
        }
    }

    internal fun markAsPersisted() {
        if (_entityState != EntityState.New) {
            return
        }

        _entityState = EntityState.Persisted
    }
}
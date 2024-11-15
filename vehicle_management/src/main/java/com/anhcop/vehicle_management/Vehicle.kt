package com.anhcop.vehicle_management

import java.util.UUID

open class Vehicle internal constructor(
    val id: String,
    name: String,
    macAddress: String,
    localIP: String,
    price: Long,
    entityState: EntityState
) {
    internal enum class EntityState {
        New, Persisted
    }

    companion object {
        internal const val NAME = "name"
        internal const val MAC_ADDRESS = "mac_address"
        internal const val LOCAL_IP = "local_ip"
        internal const val PRICE = "price"

        fun create(name: String, macAddress: String, localIP: String, price: Long): Vehicle {
            val id = UUID.randomUUID().toString()
            return Vehicle(id, name, macAddress, localIP, price, EntityState.New)
        }
    }

    private var _entityState: EntityState = entityState
    internal val entityState: EntityState get() = _entityState

    private val _changes = mutableMapOf<String, Any>()
    internal val changes: Map<String, Any> = _changes

    val isModified: Boolean get() = _changes.isNotEmpty()

    private var _name: String = name
    var name: String
        get() = (_changes[NAME] as? String) ?: _name
        set(value) {
            if (_entityState == EntityState.New) {
                _name = value
            } else {
                if (value == _name) {
                    _changes.remove(NAME)
                } else {
                    _changes[NAME] = value
                }
            }
        }

    private var _macAddress: String = macAddress
    var macAddress: String
        get() = (_changes[MAC_ADDRESS] as? String) ?: _macAddress
        set(value) {
            if (_entityState == EntityState.New) {
                _macAddress = value
            } else {
                if (value == _macAddress) {
                    _changes.remove(MAC_ADDRESS)
                } else {
                    _changes[MAC_ADDRESS] = value
                }
            }
        }

    private var _localIP: String = localIP
    var localIP: String
        get() = (_changes[LOCAL_IP] as? String) ?: _localIP
        set(value) {
            if (_entityState == EntityState.New) {
                _localIP = value
            } else {
                if (value == _localIP) {
                    _changes.remove(LOCAL_IP)
                } else {
                    _changes[LOCAL_IP] = value
                }
            }
        }


    private var _price: Long = price
    var price: Long
        get() = (_changes[PRICE] as? Long) ?: _price
        set(value) {
            if (_entityState == EntityState.New) {
                _price = value
            } else {
                if (value == _price) {
                    _changes.remove(PRICE)
                } else {
                    _changes[PRICE] = value
                }
            }
        }

    internal fun applyChanges() {
        if (entityState == EntityState.Persisted) {
            for ((key, value) in _changes) {
                when (key) {
                    NAME -> _name = value as String
                    MAC_ADDRESS -> _macAddress = value as String
                    LOCAL_IP -> _localIP = value as String
                    PRICE -> _price = value as Long
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
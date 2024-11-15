package com.anhcop.atvcontrol_owner.screens.dashboard.tabs.vehicle_list

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.http.URLProtocol
import io.ktor.http.path
import javax.inject.Inject

class VehicleControlService @Inject constructor() {
    private val client = HttpClient(Android) {
        engine {
            request {
                timeout {
                    requestTimeoutMillis = 5000
                }
            }
        }
    }

    suspend fun toggleVehiclePower(localIP: String) {
        client.get {
            url {
                protocol = URLProtocol.HTTP
                host = localIP
                path("ownerToggle")
            }
        }
    }
}
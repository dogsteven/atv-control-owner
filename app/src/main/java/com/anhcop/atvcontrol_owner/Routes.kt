package com.anhcop.atvcontrol_owner

import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object AnalyticRoute

@Serializable
data class VehicleDetailRoute(val id: String)

@Serializable
data class VehicleEditorRoute(val id: String?)

@Serializable
data class EmployeeDetailRoute(val id: String)

@Serializable
data class EmployeeEditorRoute(val id: String?)
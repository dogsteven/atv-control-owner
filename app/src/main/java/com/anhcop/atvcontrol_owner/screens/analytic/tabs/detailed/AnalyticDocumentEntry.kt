package com.anhcop.atvcontrol_owner.screens.analytic.tabs.detailed

import com.anhcop.analytic.AnalyticDocument

data class AnalyticDocumentEntry(
    val vehicleId: String,
    val vehicleName: String,
    val document: AnalyticDocument
)
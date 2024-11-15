package com.anhcop.analytic

import com.google.firebase.Timestamp

internal data class SessionHistory internal constructor(
    val price: Long,
    val numberOfTickets: Long,
    val timestamp: Timestamp,
    val employeeName: String,
    val sessionDuration: Long
)
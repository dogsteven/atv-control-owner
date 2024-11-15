package com.anhcop.analytic

data class AnalyticDocument(
    val numberOfOneTicketSessions: Int = 0,
    val numberOfTwoTicketSessions: Int = 0,
    val revenue: Long = 0L,
    val hasAnomaly: Boolean = false,
    val uptime: Long = 0L,
    val activities: List<Double> = emptyList()
)
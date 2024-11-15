package com.anhcop.analytic

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

internal class SessionHistoryRepository(
    private val firestoreFactory: () -> FirebaseFirestore
) {
    companion object {
        private const val PRICE = "price"
        private const val NUMBER_OF_TICKETS = "number_of_tickets"
        private const val TIMESTAMP = "timestamp"
        private const val EMPLOYEE_NAME = "employee_name"
        private const val SESSION_DURATION = "session_duration"

        private fun DocumentSnapshot.toSessionHistory(): SessionHistory? {
            val price = getLong(PRICE) ?: return null
            val numberOfTickets = getLong(NUMBER_OF_TICKETS) ?: return null
            val timestamp = getTimestamp(TIMESTAMP) ?: return null
            val employeeName = getString(EMPLOYEE_NAME) ?: return null
            val sessionDuration = getLong(SESSION_DURATION) ?: return null

            return SessionHistory(price, numberOfTickets, timestamp, employeeName, sessionDuration)
        }
    }

    private val vehiclesCollection: CollectionReference
        get() = firestoreFactory().collection("vehicles")

    internal suspend fun getAllGeneralSessionHistories(): List<SessionHistory> {
        return coroutineScope {
            vehiclesCollection.get(Source.SERVER).await().map { document ->
                val vehicleId = document.id

                async {
                    getAllDetailedSessionHistories(vehicleId = vehicleId)
                }
            }.awaitAll().flatten().sortedBy { it.timestamp }
        }
    }

    private suspend fun getAllDetailedSessionHistories(
        vehicleId: String
    ): List<SessionHistory> {
        return vehiclesCollection.document(vehicleId).collection("session_histories")
            .get(Source.SERVER).await().mapNotNull { document ->
                document.toSessionHistory()
            }.sortedBy { sessionHistory ->
                sessionHistory.timestamp
            }
    }

    internal suspend fun getGeneralSessionHistoriesBetween(
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): List<SessionHistory> {
        return coroutineScope {
            vehiclesCollection.get(Source.SERVER).await().map { document ->
                val vehicleId = document.id

                async {
                    getDetailedSessionHistoriesBetween(
                        vehicleId = vehicleId,
                        startTimestamp = startTimestamp,
                        endTimestamp = endTimestamp
                    )
                }
            }.awaitAll().flatten()
        }
    }

    internal suspend fun getDetailedSessionHistoriesBetween(
        vehicleId: String,
        startTimestamp: Timestamp,
        endTimestamp: Timestamp
    ): List<SessionHistory> {
        return vehiclesCollection.document(vehicleId).collection("session_histories")
            .whereGreaterThanOrEqualTo(TIMESTAMP, startTimestamp)
            .whereLessThanOrEqualTo(TIMESTAMP, endTimestamp)
            .get(Source.SERVER).await().mapNotNull { document ->
                document?.toSessionHistory()
            }.sortedBy { sessionHistory ->
                sessionHistory.timestamp
            }
    }
}
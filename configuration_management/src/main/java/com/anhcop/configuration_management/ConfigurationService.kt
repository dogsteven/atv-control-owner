package com.anhcop.configuration_management

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await

class ConfigurationService(private val firestoreFactory: () -> FirebaseFirestore) {
    companion object {
        private const val SESSION_DURATION = "session_duration"
        private const val SESSION_IS_ABOUT_TO_END_ALERT_DURATION = "session_is_about_to_end_alert_duration"
    }

    private val collection: CollectionReference
        get() = firestoreFactory().collection("configurations")

    private val sessionDurationDocument: DocumentReference
        get() = collection.document(SESSION_DURATION)

    private val sessionIsAboutToEndAlertDurationDocument: DocumentReference
        get() = collection.document(SESSION_IS_ABOUT_TO_END_ALERT_DURATION)

    suspend fun getSessionDuration(): Long {
        return sessionDurationDocument.get().await().getLong("value") ?: return 20L
    }

    suspend fun getSessionIsAboutToEndAlertDuration(): Long {
        return sessionIsAboutToEndAlertDurationDocument.get().await().getLong("value") ?: return 5L
    }

    fun updateSessionDuration(value: Long) {
        sessionDurationDocument.set(mapOf("value" to value))
    }

    fun updateSessionIsAboutToEndAlertDuration(value: Long) {
        sessionIsAboutToEndAlertDurationDocument.set(mapOf("value" to value))
    }
}
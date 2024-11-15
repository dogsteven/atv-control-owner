package com.anhcop.atvcontrol_owner.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirestoreFactory @Inject constructor() {
    fun create(): FirebaseFirestore {
        return Firebase.firestore
    }
}
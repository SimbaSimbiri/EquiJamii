package com.simbiri.equityjamii.data.objects

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.Person

object AuthUtils {
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getCurrentPerson(userId: String?, callback: (Person?) -> Unit) {
        val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
        if (userId != null) {
            userCollection.document(userId).get()
                .addOnCompleteListener { snapShotRetrieveTask ->
                    if (snapShotRetrieveTask.isSuccessful) {
                        val person = snapShotRetrieveTask.result.toObject(Person::class.java)
                        callback(person)
                    } else {
                        callback(null)
                    }
                }
        } else {
            callback(null)
        }
    }

}

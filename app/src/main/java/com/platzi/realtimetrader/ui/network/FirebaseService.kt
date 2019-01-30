package com.platzi.realtimetrader.ui.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */
class FirebaseService(val firebaseFirestore: FirebaseFirestore) {


    inline fun <reified T> getCollectionItems(collectionName: String, callback: Callback<List<T>>) {
        firebaseFirestore.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val cryptosList: List<T> = result.toObjects(T::class.java)
                    callback.onSuccess(cryptosList)
                    break
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Developer", "Error getting documents.", exception)
                callback.onFailed(exception)
            }
    }
}
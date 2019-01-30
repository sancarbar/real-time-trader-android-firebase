package com.platzi.realtimetrader.ui.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.ui.model.Crypto

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */
class FirebaseService(val firebaseFirestore: FirebaseFirestore)
{


    fun getCryptos(callback: Callback<List<Crypto>>)
    {
        firebaseFirestore.collection("cryptos")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result)
                    {

                        val cryptosList: List<Crypto> = result.toObjects(Crypto::class.java)
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
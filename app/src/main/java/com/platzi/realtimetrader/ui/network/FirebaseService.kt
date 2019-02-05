package com.platzi.realtimetrader.ui.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.ui.model.Crypto
import com.platzi.realtimetrader.ui.model.User

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */

const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

class FirebaseService(val firebaseFirestore: FirebaseFirestore)
{


    fun getCryptos(callback: Callback<List<Crypto>>)
    {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result)
                    {

                        val cryptosList = result.toObjects(Crypto::class.java)
                        callback.onSuccess(cryptosList)
                        break
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Developer", "Error getting documents.", exception)
                    callback.onFailed(exception)
                }
    }

    fun setDocument(data: Any, collectionName: String, id: String, callbackVoid: CallbackVoid)
    {
        firebaseFirestore.collection(collectionName).document(id).set(data)
                .addOnSuccessListener { callbackVoid.onSuccess() }
                .addOnFailureListener { exception ->
                    Log.w("Developer", "Error getting documents.", exception)
                    callbackVoid.onFailed(exception)
                }
    }

    fun findUserById(id: String, callback: Callback<User>)
    {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data == null)
                        callback.onSuccess(null)
                    else
                        callback.onSuccess(result.toObject(User::class.java)!!)
                }
                .addOnFailureListener { exception ->
                    Log.w("Developer", "Error getting documents.", exception)
                    callback.onFailed(exception)
                }

    }

    fun updateUser(user: User, callback: Callback<User>?)
    {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
                .update("cryptosList", user.cryptosList)
                .addOnSuccessListener { result ->
                    if (callback != null)
                        callback.onSuccess(user)
                }
                .addOnFailureListener { exception ->
                    Log.w("Developer", "Error getting documents.", exception)
                    if (callback != null)
                        callback.onFailed(exception)
                }
    }

    fun updateCrypto(crypto: Crypto)
    {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
                .update("available", crypto.available)
    }

    fun listenForUpdates(cryptos: List<Crypto>, listener: RealtimeDataListener<Crypto>)
    {
        val reference = firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
        for (crypto in cryptos)
        {
            reference.document(crypto.getDocumentId()).addSnapshotListener { snapshot, e ->
                if (e != null)
                {
                    listener.onError(e)
                }

                if (snapshot != null && snapshot.exists())
                {
                    listener.onDataChange(snapshot.toObject(Crypto::class.java)!!)
                }
            }
        }
    }

    fun listenForUpdates(user: User, listener: RealtimeDataListener<User>)
    {
        val reference = firebaseFirestore.collection(USERS_COLLECTION_NAME)

        reference.document(user.username).addSnapshotListener { snapshot, e ->
            if (e != null)
            {
                listener.onError(e)
            }

            if (snapshot != null && snapshot.exists())
            {
                listener.onDataChange(snapshot.toObject(User::class.java)!!)
            }
        }

    }


}
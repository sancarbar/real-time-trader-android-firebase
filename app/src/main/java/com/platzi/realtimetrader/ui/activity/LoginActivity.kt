package com.platzi.realtimetrader.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.platzi.realtimetrader.R
import kotlinx.android.synthetic.main.activity_login.*

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */


const val NAME_KEY = "name_key"

class LoginActivity : AppCompatActivity()
{
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val TAG = "LoginActivity"


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onStartClicked(view: View)
    {
        auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                    {
                        // Login exitóso, actualizar la vista con la información del usuario
                        Log.d(TAG, "signInAnonymously:success")
                        val user = auth.currentUser

                        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
                        intent.putExtra(NAME_KEY, nameEditText.text.toString())

                        startActivity(intent)
                        finish()

                    } else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }

}
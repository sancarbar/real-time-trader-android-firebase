package com.platzi.realtimetrader.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.ui.adapter.CryptosAdapter
import com.platzi.realtimetrader.ui.model.Crypto
import com.platzi.realtimetrader.ui.model.User
import com.platzi.realtimetrader.ui.network.Callback
import com.platzi.realtimetrader.ui.network.FirebaseService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*

class TraderActivity : AppCompatActivity()
{

    lateinit var firebaseService: FirebaseService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter()

    private var cryptosList: List<Crypto>? = null

    private var username: String? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        firebaseService = FirebaseService(FirebaseFirestore.getInstance())
        setContentView(R.layout.activity_trader)

        username = intent.extras!![USERNAME_KEY]!!.toString()
        usernameTextView.text = username


        Log.d("Developer", "username:  $username")
        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_LONG)
                    .setAction("Info", null).show()
            generateCryptoCurrenciesRandom()
        }
    }

    private fun loadCryptos()
    {
        firebaseService.getCryptos(object : Callback<List<Crypto>>
        {

            override fun onSuccess(result: List<Crypto>)
            {
                this@TraderActivity.runOnUiThread {
                    this@TraderActivity.cryptosList = result

                    cryptosAdapter.cryptosList = result
                    cryptosAdapter.notifyDataSetChanged()

                    firebaseService.findUserById(username!!, object : Callback<User>
                    {

                        override fun onSuccess(respones: User)
                        {
                            Log.d("Developer", "User:  ${respones.username}")
                            if (respones.cryptosList == null)
                            {

                                val userCryptoList = mutableListOf<Crypto>()

                                for (crypto in cryptosList!!)
                                {
                                    crypto.available = 0
                                    userCryptoList.add(crypto)
                                    addUserCryptoInfoRow(crypto)
                                }

                                respones.cryptosList = mutableListOf()
                                firebaseService.updateUser(respones, null)
                            } else
                            {
                                for (crypto in cryptosList!!)
                                {
                                    addUserCryptoInfoRow(crypto)
                                }
                            }
                        }

                        override fun onFailed(exception: Exception)
                        {
                            Log.e("Developer", "error", exception)
                        }

                    })
                }
            }

            override fun onFailed(exception: Exception)
            {

            }
        })
    }

    private fun generateCryptoCurrenciesRandom()
    {
        val amount = (1..10).random()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureRecyclerView()
    {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    fun addUserCryptoInfoRow(crypto: Crypto)
    {
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text = getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)
    }
}

package com.platzi.realtimetrader.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.ui.adapter.CryptosAdapter
import com.platzi.realtimetrader.ui.adapter.CryptosAdapterListener
import com.platzi.realtimetrader.ui.model.Crypto
import com.platzi.realtimetrader.ui.model.User
import com.platzi.realtimetrader.ui.network.Callback
import com.platzi.realtimetrader.ui.network.FirebaseService
import com.platzi.realtimetrader.ui.network.RealtimeDataListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*

class TraderActivity : AppCompatActivity(), CryptosAdapterListener
{

    lateinit var firebaseService: FirebaseService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    private var username: String? = null

    var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        firebaseService = FirebaseService(FirebaseFirestore.getInstance())
        setContentView(R.layout.activity_trader)

        username = intent.extras!![USERNAME_KEY]!!.toString()
        usernameTextView.text = username

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

            override fun onSuccess(result: List<Crypto>?)
            {
                this@TraderActivity.runOnUiThread {

                    cryptosAdapter.cryptosList = result!!
                    cryptosAdapter.notifyDataSetChanged()

                    firebaseService.findUserById(username!!, object : Callback<User>
                    {

                        override fun onSuccess(result: User?)
                        {
                            user = result
                            Log.d("Developer", "User:  ${result!!.username}")
                            if (user!!.cryptosList == null)
                            {

                                val userCryptoList = mutableListOf<Crypto>()

                                for (crypto in cryptosAdapter.cryptosList)
                                {
                                    val cryptoUser = Crypto()
                                    cryptoUser.name = crypto.name
                                    cryptoUser.available = 0
                                    cryptoUser.imageUrl = crypto.imageUrl
                                    userCryptoList.add(cryptoUser)
                                }

                                user!!.cryptosList = userCryptoList
                                firebaseService.updateUser(user!!, null)
                            }
                            reloadUserCryptos()
                            addRealtimeDatabaseListeners(user!!, cryptosAdapter.cryptosList)
                        }

                        override fun onFailed(exception: Exception)
                        {
                            Log.e("Developer", "error", exception)
                            showGeneralServerErrorMessage()
                        }

                    })
                }
            }

            override fun onFailed(exception: Exception)
            {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }
        })
    }

    private fun generateCryptoCurrenciesRandom()
    {
        val amount = (1..10).random()

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

    override fun onBuyCryptoClicked(crypto: Crypto)
    {
        for (userCrypto in user!!.cryptosList!!)
        {
            if (userCrypto.name == crypto.name)
            {
                userCrypto.available += 1
                break
            }
        }
        crypto.available--

        firebaseService.updateUser(user!!, null)
        firebaseService.updateCrypto(crypto)
    }

    fun reloadUserCryptos()
    {
        if (user != null && user!!.cryptosList != null)
        {
            infoPanel.removeAllViews()
            for (crypto in user!!.cryptosList!!)
            {
                addUserCryptoInfoRow(crypto)
            }
        }
    }

    fun addRealtimeDatabaseListeners(currentUser: User, cryptosList: List<Crypto>)
    {
        firebaseService.listenForUpdates(currentUser, object : RealtimeDataListener<User>
        {
            override fun onDataChange(updateData: User)
            {
                user = updateData
                reloadUserCryptos()
            }

            override fun onError(exception: java.lang.Exception)
            {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }

        })

        firebaseService.listenForUpdates(cryptosList, object : RealtimeDataListener<Crypto>
        {
            override fun onDataChange(updateData: Crypto)
            {
                var pos = 0
                for (crypto in cryptosAdapter.cryptosList)
                {
                    if (crypto.name.equals(updateData.name))
                    {
                        crypto.available = updateData.available
                        cryptosAdapter.notifyItemChanged(pos)
                        break
                    }
                    pos++
                }

            }

            override fun onError(exception: java.lang.Exception)
            {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    fun showGeneralServerErrorMessage()
    {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
                .setAction("Info", null).show()
        generateCryptoCurrenciesRandom()
    }

}

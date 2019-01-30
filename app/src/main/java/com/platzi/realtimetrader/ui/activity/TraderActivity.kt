package com.platzi.realtimetrader.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.ui.adapter.CryptosAdapter
import com.platzi.realtimetrader.ui.model.Crypto
import com.platzi.realtimetrader.ui.network.Callback
import com.platzi.realtimetrader.ui.network.FirebaseService
import kotlinx.android.synthetic.main.activity_trader.*

class TraderActivity : AppCompatActivity()
{

    lateinit var firebaseService: FirebaseService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        firebaseService = FirebaseService(FirebaseFirestore.getInstance())
        setContentView(R.layout.activity_trader)

        val name = intent.extras!![NAME_KEY]
        username.text = name!!.toString()
        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            generateCryptoCurrenciesRandom()
        }
    }

    private fun loadCryptos()
    {
        firebaseService.getCollectionItems("cryptos", object : Callback<List<Crypto>>
        {

            override fun onSuccess(result: List<Crypto>)
            {

                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptosList = result
                    cryptosAdapter.notifyDataSetChanged()
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

    fun configureRecyclerView()
    {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }
}

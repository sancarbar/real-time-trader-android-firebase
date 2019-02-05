package com.platzi.realtimetrader.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.ui.model.Crypto
import com.squareup.picasso.Picasso

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */
class CryptosAdapter : RecyclerView.Adapter<CryptosAdapter.ViewHolder>()
{


    var cryptosList: List<Crypto> = ArrayList()

    override fun onBindViewHolder(holder: CryptosAdapter.ViewHolder, position: Int)
    {
        val crypto = cryptosList[position]
        holder.name.text = crypto.name

        val context = holder.itemView.context
        holder.available.text =
                context.getString(R.string.available_message, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(holder.image)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.crypto_row, viewGroup, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int
    {
        return cryptosList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {

        var name = view.findViewById<TextView>(R.id.nameTextView)
        var available = view.findViewById<TextView>(R.id.availableTextView)
        var image = view.findViewById<ImageView>(R.id.image)
    }
}
package com.platzi.realtimetrader.ui.adapter

import com.platzi.realtimetrader.ui.model.Crypto

/**
 * @author Santiago Carrillo
 * 2/5/19.
 */
interface CryptosAdapterListener
{
    fun onBuyCryptoClicked(crypto: Crypto)
}
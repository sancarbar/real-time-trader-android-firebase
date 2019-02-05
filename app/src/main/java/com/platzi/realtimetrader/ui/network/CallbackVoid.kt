package com.platzi.realtimetrader.ui.network

/**
 * @author Santiago Carrillo
 * 2/5/19.
 */
interface CallbackVoid
{

    fun onSuccess()

    fun onFailed(e: Exception)
}
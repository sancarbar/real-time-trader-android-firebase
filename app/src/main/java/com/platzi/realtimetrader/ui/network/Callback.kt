package com.platzi.realtimetrader.ui.network

/**
 * @author Santiago Carrillo
 * 1/29/19.
 */
interface Callback<T>
{

    fun onSuccess(result: T)

    fun onFailed(exception: Exception)
}
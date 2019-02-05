package com.platzi.realtimetrader.ui.network

/**
 * @author Santiago Carrillo
 * 2/5/19.
 */
interface RealtimeDataListener<T>
{
    fun onDataChange(updateData: T)

    fun onError(exception: Exception)
}
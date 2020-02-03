package com.bconline.homesapp.remote

interface RetroCallback<T> {
    fun onError(t: Throwable)
    fun onSuccess(code:Int,receivedData:T)
    fun onFailure(code:Int)
}
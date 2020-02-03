package com.bconline.homesapp.remote

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val BASE_URL:String = "https://homesapp.co.kr/"
    var retrofit: Retrofit? = null

    fun get(context: Context):Retrofit?{
        if(retrofit == null){
            val okHttp:OkHttpClient = OkHttpClient.Builder().build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                //.addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}
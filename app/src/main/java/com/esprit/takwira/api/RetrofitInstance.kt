package com.esprit.takwira.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor


object RetrofitInstance {
    var ip = "172.17.1.19"
    var BASE_URL = "http://"+ip+":3000/api/"
    fun api( context: Context?) :UserApi {

        val retrofit = Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okhttpClient(context)) // Add our Okhttp client
            .build()
        //.create(UserApi::class.java)


        return retrofit.create(UserApi::class.java)
    }

    private fun okhttpClient(context: Context?): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .build()
    }



}


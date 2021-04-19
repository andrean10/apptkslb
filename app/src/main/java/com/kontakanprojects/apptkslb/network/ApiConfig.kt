package com.kontakanprojects.apptkslb.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        // hosting
        val URL = "http://tkslb.rproject-dev.com"

        private val ENDPOINT = "$URL/api/"

        private fun client(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        fun getApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
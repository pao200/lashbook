package com.lashbook.wearable.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LashBookApiClient {

    /*
     * En un emulador Android, 10.0.2.2 representa
     * la computadora donde se ejecuta Spring Boot.
     */
    private const val BASE_URL =
    "https://lashbook-backend.onrender.com/api/"

    private val interceptorRegistro =
        HttpLoggingInterceptor().apply {
            level =
                HttpLoggingInterceptor.Level.BODY
        }

    private val clienteHttp =
        OkHttpClient.Builder()
            .addInterceptor(interceptorRegistro)
            .build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteHttp)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val api: LashBookApi =
        retrofit.create(
            LashBookApi::class.java
        )
}
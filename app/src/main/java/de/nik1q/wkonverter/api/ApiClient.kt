package de.nik1q.wkonverter.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://exchange-rates.abstractapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
}
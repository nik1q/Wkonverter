package de.nik1q.wkonverter.api

import de.nik1q.wkonverter.models.RateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v1/live")
    fun getExchangeRates(
        @Query("api_key") apiKey: String,
        @Query("base") baseCurrency: String = "EUR"
    ): Call<RateResponse>
}

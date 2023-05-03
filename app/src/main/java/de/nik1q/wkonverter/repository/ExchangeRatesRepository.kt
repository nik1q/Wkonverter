package de.nik1q.wkonverter.repository

import de.nik1q.wkonverter.api.ApiClient
import de.nik1q.wkonverter.models.RateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExchangeRatesRepository(private val apiKey: String) {
    private val apiService = ApiClient().apiService

    fun getExchangeRates(base: String, onResult: (RateResponse?, Throwable?) -> Unit) {
        apiService.getExchangeRates(apiKey, base).enqueue(object : Callback<RateResponse> {
            override fun onResponse(call: Call<RateResponse>, response: Response<RateResponse>) {
                if (response.isSuccessful) {
                    val rateResponse = response.body()
                    onResult(rateResponse, null)
                } else {
                    onResult(null, Exception("Error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<RateResponse>, t: Throwable) {
                onResult(null, t)
            }
        })
    }
}


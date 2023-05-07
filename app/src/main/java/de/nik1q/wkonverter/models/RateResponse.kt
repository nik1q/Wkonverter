package de.nik1q.wkonverter.models


data class RateResponse(
    val base: String,
    val exchange_rates: ExchangeRates,
    val last_updated: Long
)



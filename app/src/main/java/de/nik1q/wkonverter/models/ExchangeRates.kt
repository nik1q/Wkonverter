package de.nik1q.wkonverter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("exchange_rates", primaryKeys = ["base", "last_updated"])
data class ExchangeRates(
    var base: String,

    var last_updated: Int,

    val EUR: Double=1.0,
    val USD: Double=1.0,
    val XRP: Double=1.0,
    val ZAR: Double=1.0
)
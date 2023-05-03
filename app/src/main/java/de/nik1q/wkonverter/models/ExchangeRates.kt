package de.nik1q.wkonverter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("exchange_rates", primaryKeys = ["base", "last_updated"])
data class ExchangeRates(
    var base: String,

    var last_updated: Int,

    val EUR: Double=1.0,
    val USD: Double=1.0,
    val RUB: Double=1.0,
    val TRY: Double=1.0
)
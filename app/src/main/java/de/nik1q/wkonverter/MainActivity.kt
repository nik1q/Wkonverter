package de.nik1q.wkonverter

import android.content.res.XmlResourceParser
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.nik1q.wkonverter.database.RateResponseDatabase
import de.nik1q.wkonverter.databinding.ActivityMainBinding
import de.nik1q.wkonverter.models.LastUpdatedHelper
import de.nik1q.wkonverter.repository.ExchangeRatesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val apiKey = "4a88a5ab129e44abb6af1cc93d15f594"
    private val repository = ExchangeRatesRepository(apiKey)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = RateResponseDatabase.getDatabase(applicationContext)
        db.openHelper.writableDatabase

        // pressing "los" buttton
        binding.btGetKurs.setOnClickListener {
            val array = arrayOf("EUR", "USD", "XRP", "ZAR").forEach {
                Thread.sleep(2000)
                repository.getExchangeRates(it) { exchangeRates, error ->
                    if (exchangeRates != null) {
                        val base = exchangeRates.base
                        val rates = exchangeRates.exchange_rates
                        val lastUpdated = exchangeRates.last_updated

                        rates.base = base
                        rates.last_updated = lastUpdated
                        CoroutineScope(Dispatchers.IO).launch {
                            db.rateResponseDao().insert(rates)
                        }


                        // processing date into a log
                        Log.d("Exchange Rates", "Base currency: $base")
                        Log.d("Exchange Rates", "Exchange rates: $rates")
                        Log.d("Exchange Rates", "Last updated: $lastUpdated")
                        //last update Time
                        val dateString =
                            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(
                                Date(lastUpdated.toLong())
                            )
                        LastUpdatedHelper.updateLastUpdated(dateString, binding.txLastUpd)
                    } else {
                        Toast.makeText(this, "Error: ${error?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}


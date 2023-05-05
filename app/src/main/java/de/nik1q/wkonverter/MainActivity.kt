package de.nik1q.wkonverter

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.nik1q.wkonverter.database.RateResponseDatabase
import de.nik1q.wkonverter.databinding.ActivityMainBinding
import de.nik1q.wkonverter.models.LastUpdatedHelper
import de.nik1q.wkonverter.repository.ExchangeRatesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            val array = arrayOf("EUR", "USD", "RUB", "TRY").forEach {
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
                        val formattedLastUpdated = formatUnixTimestamp(lastUpdated)
                        LastUpdatedHelper.updateLastUpdated(formattedLastUpdated, binding.txLastUpd)

                    } else {
                        Toast.makeText(this, "Error: ${error?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // Set Currency Rate
            var lastClickedButton: Button? = null
            var curExcRate = ""
            binding.btSelectRub.setOnClickListener {
                curExcRate = "RUB"
                Toast.makeText(this, "Währung ist: $curExcRate", Toast.LENGTH_SHORT).show()
                binding.btSelectRub.setBackgroundColor(resources.getColor(R.color.selected_button_color))
                lastClickedButton?.setBackgroundColor(resources.getColor(R.color.default_button_color))
                lastClickedButton = binding.btSelectRub
            }

            binding.btSelectTry.setOnClickListener {
                curExcRate = "TRY"
                Toast.makeText(this, "Währung ist: $curExcRate", Toast.LENGTH_SHORT).show()
                binding.btSelectTry.setBackgroundColor(resources.getColor(R.color.selected_button_color))
                lastClickedButton?.setBackgroundColor(resources.getColor(R.color.default_button_color))
                lastClickedButton = binding.btSelectTry
            }

            binding.btSelectUsd.setOnClickListener {
                curExcRate = "USD"
                Toast.makeText(this, "Währung ist: $curExcRate", Toast.LENGTH_SHORT).show()
                binding.btSelectUsd.setBackgroundColor(resources.getColor(R.color.selected_button_color))
                lastClickedButton?.setBackgroundColor(resources.getColor(R.color.default_button_color))
                lastClickedButton = binding.btSelectUsd
            }

            binding.btSelectEur.setOnClickListener {
                curExcRate = "EUR"
                Toast.makeText(this, "Währung ist: $curExcRate", Toast.LENGTH_SHORT).show()
                binding.btSelectEur.setBackgroundColor(resources.getColor(R.color.selected_button_color))
                lastClickedButton?.setBackgroundColor(resources.getColor(R.color.default_button_color))
                lastClickedButton = binding.btSelectEur
            }

            // take the Value entered by the User
            //val edGetValue = binding.edGetValue.text.toString().toDouble()

            binding.btGetResult.setOnClickListener {
                // take the Value entered by the User
                val edGetValueText = binding.edGetValue.text.toString()
                if (edGetValueText.isEmpty()) {
                    // display an error message or handle the error in some other way
                    return@setOnClickListener
                }
                val edGetValue = binding.edGetValue.text.toString().toDouble()

                CoroutineScope(Dispatchers.IO).launch {
                    val curRate = db.rateResponseDao().getRateByBase(curExcRate)
                    val usdCurRate = curRate?.USD ?: 0.0
                    val eurCurRate = curRate?.EUR ?: 0.0
                    val rubCurRate = curRate?.RUB ?: 0.0
                    val tryCurRate = curRate?.TRY ?: 0.0
                    withContext(Dispatchers.Main) {
                        val usdResultConv = usdCurRate * edGetValue
                        val eurResultConv = eurCurRate * edGetValue
                        val rubResultConv = rubCurRate * edGetValue
                        val tryResultConv = tryCurRate * edGetValue

                        // Set the results in the TextViews
                        binding.textViewUsd.text = "$ %.2f".format(usdResultConv)
                        binding.textViewEur.text = "€ %.2f".format(eurResultConv)
                        binding.textViewRub.text = "₽ %.2f".format(rubResultConv)
                        binding.textViewTry.text = "₺ %.2f".format(tryResultConv)
                    }
                }
            }




        }
    }

    // Unix-Time Convertor
    private fun formatUnixTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timestamp * 1000))
    }
}


package de.nik1q.wkonverter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
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

        // pressing the "los" button
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
                        // logging of data on API response received
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
            // Selecting the current base currency for conversion
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

            // if no currency is selected - display a message
            binding.btGetResult.setOnClickListener {
                if (curExcRate.isBlank()) {
                    Toast.makeText(this, "Wählen Sie Währung zuerst", Toast.LENGTH_SHORT).show()
                    binding.edGetValue.isEnabled = false
                } else {
                    binding.edGetValue.isEnabled = true
                    val edGetValueText = binding.edGetValue.text.toString()
                    if (edGetValueText.isEmpty()) {
                        return@setOnClickListener
                    }
                }
                // This code converts currencies based on the value entered by the user
                // and currency exchange rates from the database.
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
                        // Writing the result in TextViews
                        binding.textViewUsd.text = "$ %.2f".format(usdResultConv)
                        binding.textViewEur.text = "€ %.2f".format(eurResultConv)
                        binding.textViewRub.text = "₽ %.2f".format(rubResultConv)
                        binding.textViewTry.text = "₺ %.2f".format(tryResultConv)
                    }
                }
            }

            // keyboard hiding
            binding.edGetValue.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.edGetValue.windowToken, 0)
                    binding.btGetResult.performClick()
                    true
                } else {
                    false
                }
            }

            binding.root.setOnClickListener {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.edGetValue.windowToken, 0)
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


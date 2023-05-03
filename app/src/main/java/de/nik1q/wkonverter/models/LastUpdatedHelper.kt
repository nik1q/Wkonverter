package de.nik1q.wkonverter.models

import android.widget.TextView
import java.lang.reflect.Type

// get date of last update
class LastUpdatedHelper {
    companion object {
        fun updateLastUpdated(dateString: String, textView: TextView) {
            textView.setText("Letzte Aktualisierung: $dateString")
        }

        fun <T:Any> T.toMap() : Map<String,Any?> {
            return ExchangeRates::class.java.declaredFields
                .filter { field -> field.type == Double::class}
                .associate { it.name to it.get(this) }
        }
    }
}



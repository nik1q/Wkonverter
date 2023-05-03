package de.nik1q.wkonverter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.nik1q.wkonverter.models.ExchangeRates
import de.nik1q.wkonverter.models.RateResponse

@Dao
interface RateResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rateResponseEntity : ExchangeRates)

    @Query("SELECT * FROM exchange_rates WHERE base = :base")
    suspend fun getRateByBase(base : String) : ExchangeRates?

//    @Query("SELECT MAX(last_updated) FROM exchange_rates WHERE base = :base")
//    suspend fun getLastRates(base: String): ExchangeRates
}
package de.nik1q.wkonverter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.nik1q.wkonverter.models.ExchangeRates

@Database(entities = [ExchangeRates::class, ], version = 1)
abstract class RateResponseDatabase:RoomDatabase() {
    abstract fun rateResponseDao(): RateResponseDao

    companion object {
        @Volatile
        private var INSTANCE: RateResponseDatabase? = null

        fun getDatabase(context: Context): RateResponseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RateResponseDatabase::class.java,
                    "my_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
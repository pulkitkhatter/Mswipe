package com.example.mswipe

import Aadhaar
import AadhaarDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Aadhaar::class], version = 1, exportSchema = false)
abstract class AadhaarDatabase : RoomDatabase() {
    abstract fun aadhaarDao(): AadhaarDao

    companion object {
        @Volatile
        private var INSTANCE: AadhaarDatabase? = null

        fun getDatabase(context: Context): AadhaarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AadhaarDatabase::class.java,
                    "aadhaar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

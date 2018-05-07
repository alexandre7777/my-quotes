package com.alexandre.myquotes.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.alexandre.myquotes.dao.QuoteDataDao
import com.alexandre.myquotes.dao.UserDataDao
import com.alexandre.myquotes.model.QuoteData
import com.alexandre.myquotes.model.UserData

@Database(entities = arrayOf(UserData::class, QuoteData::class), version = 1, exportSchema = false)
abstract class QuotesDataBase : RoomDatabase() {

    abstract fun userDataDao(): UserDataDao

    abstract fun quoteDataDao(): QuoteDataDao

    companion object {
        private var INSTANCE: QuotesDataBase? = null

        fun getInstance(context: Context): QuotesDataBase? {
            if (INSTANCE == null) {
                synchronized(QuotesDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuotesDataBase::class.java, "quotes.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
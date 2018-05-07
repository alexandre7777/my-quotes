package com.alexandre.myquotes.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.alexandre.myquotes.model.QuoteData

@Dao
interface QuoteDataDao {

    @Query("SELECT * from quoteData")
    fun getAll(): List<QuoteData>

    @Insert(onConflict = REPLACE)
    fun insert(quoteData: QuoteData)

    @Query("DELETE from userData")
    fun deleteAll()
}

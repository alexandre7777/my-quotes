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

    @Query("SELECT * from quoteData WHERE login = :login ORDER BY position")
    fun getAllByLogin(login : String): List<QuoteData>

    @Query("SELECT id from quoteData WHERE login = :login ORDER BY position")
    fun getPositionByLogin(login : String): List<Int>

    @Query("UPDATE quoteData SET position = :newPosition WHERE position = :oldPosition AND login = :login")
    fun updatePosition(newPosition : Int, oldPosition : Int, login : String)

    @Insert(onConflict = REPLACE)
    fun insert(quoteData: QuoteData)

    @Query("DELETE from userData")
    fun deleteAll()
}

package com.alexandre.myquotes.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.alexandre.myquotes.model.UserData

@Dao
interface UserDataDao {

    @Query("SELECT * from userData")
    fun getAll(): List<UserData>

    @Query("SELECT * from userData WHERE login = :login AND password = :password")
    fun getByLoginAndPassword(login : String, password : String): List<UserData>

    @Query("SELECT * from userData WHERE login = :login")
    fun getByLogin(login : String): List<UserData>

    @Insert(onConflict = REPLACE)
    fun insert(userData: UserData)

    @Query("UPDATE userData SET urlPicto = :urlPicto, favCount = :favCount WHERE login = :login")
    fun updateUserByLogin(urlPicto : String, favCount : Int, login : String)

    @Query("DELETE from userData")
    fun deleteAll()

    @Query("DELETE from userData WHERE login = :login")
    fun deleteByLogin(login : String)
}

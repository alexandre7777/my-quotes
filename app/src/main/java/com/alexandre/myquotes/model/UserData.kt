package com.alexandre.myquotes.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo

@Entity(tableName = "userData")
data class UserData(@PrimaryKey(autoGenerate = true) var id: Long?,
                    @ColumnInfo(name = "login") var login: String,
                    @ColumnInfo(name = "password") var password: String,
                    @ColumnInfo(name = "urlPicto") var urlPicto: String,
                    @ColumnInfo(name = "favCount") var favCount: Int) {

}
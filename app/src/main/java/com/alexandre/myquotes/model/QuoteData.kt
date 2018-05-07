package com.alexandre.myquotes.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "quoteData")
data class QuoteData(@PrimaryKey(autoGenerate = false) var id: Int,
        @ColumnInfo(name = "dialogue") var dialogue: Boolean = false,
        @ColumnInfo(name = "is_private") var is_private: Boolean = false,
        @ColumnInfo(name = "tags") var tags: String,
        @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "favorites_count") var favorites_count: Int,
        @ColumnInfo(name = "upvotes_count") var upvotes_count: Int,
        @ColumnInfo(name = "downvotes_count") var downvotes_count: Int,
        @ColumnInfo(name = "author") var author: String,
        @ColumnInfo(name = "author_permalink") var author_permalink: String,
        @ColumnInfo(name = "body") var body: String,
        @ColumnInfo(name = "position") var position: Int,
        @ColumnInfo(name = "login") var login: String
)
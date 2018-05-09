package com.alexandre.myquotes.model

data class VideoQuote(
        val urlVideo:String,
        val duration: String,
        val actors: String
) : AbstractQuote()
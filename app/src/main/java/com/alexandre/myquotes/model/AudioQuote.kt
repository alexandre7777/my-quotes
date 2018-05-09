package com.alexandre.myquotes.model

data class AudioQuote(
        val urlVideo:String,
        val duration: String,
        val speakers: String
) : AbstractQuote()
package com.alexandre.myquotes.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
        @SerializedName("User-Token") val userToken: String,
        val login: String,
        val email: String
)
package com.alexandre.myquotes.model


data class UserInfo(
    val login: String,
    val pic_url: String,
    val public_favorites_count: Int,
    val following: Int,
    val followers: Int,
    val pro: Boolean,
    val account_details: AccountDetails
)

data class AccountDetails(
    val email: String,
    val private_favorites_count: Int
)
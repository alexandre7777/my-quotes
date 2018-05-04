package com.alexandre.myquotes.view.quoteslist

import android.arch.lifecycle.ViewModel
import com.alexandre.myquotes.model.Quotes
import com.alexandre.myquotes.model.UserInfo

class QuotesListViewModel : ViewModel() {

    public var userInfo : UserInfo? = null

    public var userQuotes : Quotes? = null

}
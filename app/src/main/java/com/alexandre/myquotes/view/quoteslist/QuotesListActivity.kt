package com.alexandre.myquotes.view.quoteslist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alexandre.myquotes.R
import com.alexandre.myquotes.data.webservice.LoginService
import io.reactivex.disposables.Disposable

class QuotesListActivity  : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val loginService by lazy {
        LoginService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_list)
    }
}
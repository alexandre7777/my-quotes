package com.alexandre.myquotes.application

import android.app.Application
import com.alexandre.myquotes.BuildConfig
import com.facebook.stetho.Stetho

class QuotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initStetho()
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}
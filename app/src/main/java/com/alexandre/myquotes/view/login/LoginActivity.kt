package com.alexandre.myquotes.view.login

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alexandre.myquotes.R
import com.alexandre.myquotes.data.webservice.LoginService
import com.alexandre.myquotes.model.User
import com.alexandre.myquotes.model.UserRequest
import com.alexandre.myquotes.view.quoteslist.QuotesListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val loginService by lazy {
        LoginService.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submit.setOnClickListener{
            beginLogin()
        }
    }

    private fun beginLogin() {

        disposable = loginService.loginCheck(UserRequest(User(ed_login.text.toString(), ed_password.text.toString())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> Toast.makeText(this, result.userToken, Toast.LENGTH_SHORT).show()
                            Log.d("TAG", result.userToken)
                            val sharedPref = getPreferences(Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString(getString(R.string.PREF_USER_SESSION), result.userToken)
                                apply()
                            }
                            val intent = Intent(this, QuotesListActivity::class.java)
                            startActivity(intent)
                        },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                            Log.d("TAG", error.message)}
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

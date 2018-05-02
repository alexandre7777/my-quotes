package com.alexandre.myquotes.view.quoteslist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.alexandre.myquotes.R
import com.alexandre.myquotes.data.webservice.UserInfoService
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_quotes_list.*

/**
 * Second activity to display user logo, name and fav quotes
 */
class QuotesListActivity  : AppCompatActivity() {

    private var disposable: Disposable? = null

    private lateinit var quotesListViewModel : QuotesListViewModel

    private val userInfoService by lazy {
        UserInfoService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_list)

        quotesListViewModel = ViewModelProviders.of(this).get(QuotesListViewModel::class.java)

        val userSession = intent.getStringExtra(getString(R.string.USER_SESSION))
        val userLogin = intent.getStringExtra(getString(R.string.USER_LOGIN))

        if(quotesListViewModel.userInfo != null)
        {
            displayUserInfo()
        }
        else {
            requestUSerInfo(userSession, userLogin)
        }
    }

    private fun requestUSerInfo(userSession : String, userLogin : String) {

        disposable = userInfoService.getUserInfo(userSession, userLogin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            quotesListViewModel.userInfo = result
                            displayUserInfo()

                        },
                        { error -> Toast.makeText(this, getText(R.string.network_error), Toast.LENGTH_SHORT).show()
                            Log.d("Network", "Error : " + error.message)}
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    /**
     * Display picto login and fav number of the user
     */
    private fun displayUserInfo() {

        Glide.with(this).load(quotesListViewModel.userInfo?.pic_url).into(iv_user_img)

        txt_user_name.text = quotesListViewModel.userInfo?.login

        txt_user_nb_fav.text = quotesListViewModel.userInfo?.public_favorites_count.toString()
    }
}
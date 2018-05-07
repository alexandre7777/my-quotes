package com.alexandre.myquotes.view.quoteslist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.alexandre.myquotes.R
import com.alexandre.myquotes.data.webservice.QuotesService
import com.alexandre.myquotes.data.webservice.UserInfoService
import com.alexandre.myquotes.database.QuotesDataBase
import com.alexandre.myquotes.model.Quote
import com.alexandre.myquotes.model.QuoteData
import com.alexandre.myquotes.view.quoteslist.list.QuoteAdapter
import com.alexandre.myquotes.worker.DbWorkerThread
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

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var quotesListViewModel : QuotesListViewModel

    private lateinit var adapter: QuoteAdapter

    private var mDb: QuotesDataBase? = null

    private lateinit var mDbWorkerThread: DbWorkerThread

    private val mUiHandler = Handler()

    private val userInfoService by lazy {
        UserInfoService.create()
    }

    private val quotesService by lazy {
        QuotesService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes_list)

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        mDb = QuotesDataBase.getInstance(this)

        quotesListViewModel = ViewModelProviders.of(this).get(QuotesListViewModel::class.java)

        linearLayoutManager = LinearLayoutManager(this)
        quotes_list.layoutManager = linearLayoutManager

        val userSession = intent.getStringExtra(getString(R.string.USER_SESSION))
        val userLogin = intent.getStringExtra(getString(R.string.USER_LOGIN))

        if(quotesListViewModel.userInfo != null)
        {
            displayUserInfo()
        }
        else {
            requestUSerInfo(userSession, userLogin)

        }

        if(quotesListViewModel.userQuotes != null)
        {
            displayUserQuotes()
        }
        else
        {
            requestQuotesService(userLogin)
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
                            updateUserDataInDb(result.pic_url, result.public_favorites_count, result.login)

                        },
                        { error -> Toast.makeText(this, getText(R.string.network_error), Toast.LENGTH_SHORT).show()
                            Log.d("Network", "Error : " + error.message)}
                )
    }


    private fun requestQuotesService(userName : String) {

        disposable = quotesService.getQuotes(userName, "user")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            quotesListViewModel.userQuotes = result
                            displayUserQuotes()
                            insertUserDataInDb(result.quotes, userName)
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

    private  fun displayUserQuotes() {
        adapter = QuoteAdapter(quotesListViewModel.userQuotes?.quotes)
        quotes_list.adapter = adapter
    }

    private fun insertUserDataInDb(quotes : ArrayList<Quote>, login : String) {

        for (quote in quotes) {
            val quoteData = QuoteData(quote.id, quote.dialogue, quote.private, quote.tags.joinToString(), quote.url, quote.favorites_count, quote.upvotes_count, quote.downvotes_count, quote.author, quote.author_permalink, quote.body, 0, login)
            val task = Runnable { mDb?.quoteDataDao()?.insert(quoteData) }
            mDbWorkerThread.postTask(task)
        }
    }

    private fun updateUserDataInDb(urlPicto : String, favCount : Int, login : String) {
        val task = Runnable { mDb?.userDataDao()?.updateUserByLogin(urlPicto, favCount, login) }
        mDbWorkerThread.postTask(task)
    }

    override fun onDestroy() {
        super.onDestroy()
        QuotesDataBase.destroyInstance()
        mDbWorkerThread.quit()
    }
}
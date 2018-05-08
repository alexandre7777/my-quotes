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
import com.alexandre.myquotes.model.*
import com.alexandre.myquotes.view.quoteslist.list.QuoteAdapter
import com.alexandre.myquotes.worker.DbWorkerThread
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_quotes_list.*
import android.support.v7.widget.helper.ItemTouchHelper



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

        if(userSession != null && userSession.isNotEmpty()) {

            if (quotesListViewModel.userInfo != null) {
                displayUserInfo()
            } else {
                requestUSerInfo(userSession, userLogin)

            }

            if (quotesListViewModel.userQuotes != null) {
                displayUserQuotes(userLogin)
            } else {
                requestQuotesService(userLogin)
            }
        }
        else
        {
            fetchUserDataFromDb(userLogin)

            fetchQuoteDataFromDb(userLogin)
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        QuotesDataBase.destroyInstance()
        mDbWorkerThread.quit()
    }

    /**
     * Network request to retrieve nickname, picture, count of fav quotes
     */
    private fun requestUSerInfo(userSession : String, userLogin : String) {

        disposable = userInfoService.getUserInfo(userSession, userLogin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            quotesListViewModel.userInfo = result
                            displayUserInfo()
                            //save user in db
                            updateUserDataInDb(result.pic_url, result.public_favorites_count, result.login)

                        },
                        { error -> Toast.makeText(this, getText(R.string.network_error), Toast.LENGTH_SHORT).show()
                            Log.d("Network", "Error : " + error.message)}
                )
    }


    /**
     * Network request to retrieve quotes for the user
     */
    private fun requestQuotesService(userName : String) {

        disposable = quotesService.getQuotes(userName, "user")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->

                            fetchQuotePositionDataFromDb(userName, result.quotes)
                        },
                        { error -> Toast.makeText(this, getText(R.string.network_error), Toast.LENGTH_SHORT).show()
                            Log.d("Network", "Error : " + error.message)}
                )
    }



    /**
     * Display picto login and fav number of the user
     */
    private fun displayUserInfo() {

        Glide.with(this).load(quotesListViewModel.userInfo?.pic_url).into(iv_user_img)

        txt_user_name.text = quotesListViewModel.userInfo?.login

        txt_user_nb_fav.text = quotesListViewModel.userInfo?.public_favorites_count.toString()
    }

    private  fun displayUserQuotes(userName : String) {
        adapter = QuoteAdapter(quotesListViewModel.userQuotes?.quotes) { p1:Int, p2:Int -> updatePosition(p1, p2, userName) }
        quotes_list.adapter = adapter
        val callback = QuoteAdapter.QuoteTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(quotes_list)
    }

    private fun insertQuotesDataInDb(quotes : ArrayList<Quote>, login : String) {
        var position = 0
        for (quote in quotes) {
            val quoteData = QuoteData(quote.id, quote.dialogue, quote.private, quote.tags.joinToString(), quote.url, quote.favorites_count, quote.upvotes_count, quote.downvotes_count, quote.author, quote.author_permalink, quote.body, position, login)
            position++
            val task = Runnable { mDb?.quoteDataDao()?.insert(quoteData) }
            mDbWorkerThread.postTask(task)
        }
    }

    private fun updateUserDataInDb(urlPicto : String, favCount : Int, login : String) {
        val task = Runnable { mDb?.userDataDao()?.updateUserByLogin(urlPicto, favCount, login) }
        mDbWorkerThread.postTask(task)
    }

    /**
     * Database request to retrieve nickname, picture, count of fav quotes
     */
    private fun fetchUserDataFromDb(login : String) {
        val task = Runnable {
            val userData =
                    mDb?.userDataDao()?.getByLogin(login)
            mUiHandler.post({
                if (userData == null || userData?.size == 0) {
                    Toast.makeText(this, getString(R.string.missing_offline_data), Toast.LENGTH_SHORT).show()
                } else {
                    quotesListViewModel.userInfo = UserInfo(userData[0].login, userData[0].urlPicto, userData[0].favCount, 0, 0, false, AccountDetails("", 0))
                    displayUserInfo()
                }
            })
        }
        mDbWorkerThread.postTask(task)
    }

    /**
     * Database request to retrieve quotes for the user
     */
    private fun fetchQuoteDataFromDb(login : String) {
        val task = Runnable {
            val quotesData =
                    mDb?.quoteDataDao()?.getAllByLogin(login)
            mUiHandler.post({
                if (quotesData == null || quotesData?.size == 0) {
                    Toast.makeText(this, getString(R.string.missing_offline_data), Toast.LENGTH_SHORT).show()
                } else {
                    val quotes: ArrayList<Quote> = ArrayList()
                    for (quoteData in quotesData)
                    {
                        quotes.add(Quote(quoteData.id, quoteData.dialogue, quoteData.is_private, listOf(quoteData.tags), quoteData.url,
                                quoteData.favorites_count, quoteData.upvotes_count, quoteData.downvotes_count, quoteData.author, quoteData.author_permalink, quoteData.body))
                    }

                    quotesListViewModel.userQuotes = Quotes(1, true, quotes)
                    displayUserQuotes(login)
                }
            })
        }
        mDbWorkerThread.postTask(task)
    }

    /**
     * Database request to retrieve quotes position for the user
     */
    private fun fetchQuotePositionDataFromDb(login : String, quotes : ArrayList<Quote>) {
        val task = Runnable {
            val quoteIdsData =
                    mDb?.quoteDataDao()?.getPositionByLogin(login)
            mUiHandler.post({
                if (quoteIdsData == null || quoteIdsData?.size == 0) {
                    //No quotes stored in db display and insert by the server order
                    insertQuotesDataInDb(quotes, login)
                    quotesListViewModel.userQuotes = Quotes(1, true, quotes)
                    displayUserQuotes(login)
                } else {
                    //Quotes stored in db display and insert by the db order
                    val _quotes: ArrayList<Quote> = ArrayList()
                    for (id in quoteIdsData)
                    {
                        var currentQuote: Quote? = null
                        for (quote in quotes)
                        {
                            if(id == quote.id)
                                currentQuote = quote
                        }

                        if (currentQuote != null) {
                            _quotes.add(Quote(currentQuote.id, currentQuote.dialogue, currentQuote.private, currentQuote.tags, currentQuote.url,
                                    currentQuote.favorites_count, currentQuote.upvotes_count, currentQuote.downvotes_count, currentQuote.author, currentQuote.author_permalink, currentQuote.body))
                        }

                    }
                    insertQuotesDataInDb(_quotes, login)

                    quotesListViewModel.userQuotes = Quotes(1, true, _quotes)
                    displayUserQuotes(login)
                }
            })
        }
        mDbWorkerThread.postTask(task)
    }

    private fun updatePosition(newPostion: Int, oldPosition: Int, login : String) {
        val task = Runnable { mDb?.quoteDataDao()?.updatePosition(-1, oldPosition, login) }
        mDbWorkerThread.postTask(task)
        val task1 = Runnable { mDb?.quoteDataDao()?.updatePosition(oldPosition, newPostion, login) }
        mDbWorkerThread.postTask(task1)
        val task2 = Runnable { mDb?.quoteDataDao()?.updatePosition(newPostion, -1, login) }
        mDbWorkerThread.postTask(task2)
    }
}
package com.alexandre.myquotes.view.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.alexandre.myquotes.R
import com.alexandre.myquotes.data.webservice.LoginService
import com.alexandre.myquotes.database.QuotesDataBase
import com.alexandre.myquotes.model.User
import com.alexandre.myquotes.model.UserData
import com.alexandre.myquotes.model.UserRequest
import com.alexandre.myquotes.view.quoteslist.QuotesListActivity
import com.alexandre.myquotes.worker.DbWorkerThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest

/**
 * First activity to log the user
 */
class LoginActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private var mDb: QuotesDataBase? = null

    private lateinit var mDbWorkerThread: DbWorkerThread

    private val mUiHandler = Handler()

    private val loginService by lazy {
        LoginService.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        mDb = QuotesDataBase.getInstance(this)

        submit.setOnClickListener{
            submit.isEnabled = false
            beginLogin()
        }
    }

    private fun beginLogin() {

        if(isConnectedToInternet()) {

            disposable = loginService.loginCheck(UserRequest(User(ed_login.text.toString(), ed_password.text.toString())))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                if (result.userToken != null && result.login != null) {
                                    val intent = Intent(this, QuotesListActivity::class.java)
                                    intent.putExtra(getString(R.string.USER_SESSION), result.userToken)
                                    intent.putExtra(getString(R.string.USER_LOGIN), result.login)
                                    startActivity(intent)
                                    deleteUserDataInDb(ed_login.text.toString().toLowerCase())
                                    insertUserDataInDb(ed_login.text.toString().toLowerCase(), hashString("SHA-256", ed_password.text.toString().toLowerCase()))
                                }
                                submit.isEnabled = true
                            },
                            { error ->
                                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                                submit.isEnabled = true
                                Log.d("Network", error.message)
                            }
                    )
        }
        else
        {
            fetchUserDataFromDb(ed_login.text.toString().toLowerCase(), hashString("SHA-256", ed_password.text.toString().toLowerCase()))
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private fun fetchUserDataFromDb(login : String, password : String) {
        val task = Runnable {
            val userData =
                    mDb?.userDataDao()?.getByLoginAndPassword(login, password)
            mUiHandler.post({
                if (userData == null || userData?.size == 0) {
                    Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, QuotesListActivity::class.java)
                    intent.putExtra(getString(R.string.USER_LOGIN), login)
                    startActivity(intent)
                    submit.isEnabled = true
                }
            })
        }
        mDbWorkerThread.postTask(task)
    }

    private fun insertUserDataInDb(login : String, password : String) {
        val userData = UserData(null, login, password, "", 0)
        val task = Runnable { mDb?.userDataDao()?.insert(userData) }
        mDbWorkerThread.postTask(task)
    }

    private fun deleteUserDataInDb(login : String) {
        val task = Runnable { mDb?.userDataDao()?.deleteByLogin(login) }
        mDbWorkerThread.postTask(task)
    }


    fun Context.isConnectedToInternet(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        QuotesDataBase.destroyInstance()
        mDbWorkerThread.quit()
    }
}

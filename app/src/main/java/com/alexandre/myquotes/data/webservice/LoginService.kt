package com.alexandre.myquotes.data.webservice

import com.alexandre.myquotes.BuildConfig
import com.alexandre.myquotes.model.LoginResponse
import com.alexandre.myquotes.model.User
import com.alexandre.myquotes.model.UserRequest
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Body
import retrofit2.http.Headers


interface LoginService {

    @Headers(
        "Authorization: Token token=\"2e0289016652fa743cad0f21a74ccf4b\"",
        "Content-Type: application/json")
    @POST("/api/session/")
    fun loginCheck(@Body user: UserRequest): Observable<LoginResponse>

    companion object {
        fun create(): LoginService {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.SERVER_URL)
                    .client(client)
                    .build()

            return retrofit.create(LoginService::class.java)
        }
    }

}
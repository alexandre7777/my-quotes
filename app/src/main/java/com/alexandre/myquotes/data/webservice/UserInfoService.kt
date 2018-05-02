package com.alexandre.myquotes.data.webservice

import com.alexandre.myquotes.BuildConfig
import com.alexandre.myquotes.model.UserInfo
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserInfoService {

    @Headers(
            "Authorization: Token token=\"2e0289016652fa743cad0f21a74ccf4b\"",
            "Content-Type: application/json")
    @GET("/api/users/{userName}")
    fun getUserInfo(@Header("User-Token") userToken : String, @Path("userName") userName : String ): Observable<UserInfo>

    companion object {
        fun create(): UserInfoService {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.SERVER_URL)
                    .build()

            return retrofit.create(UserInfoService::class.java)
        }
    }

}
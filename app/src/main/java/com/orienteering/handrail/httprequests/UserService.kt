package com.orienteering.handrail.httprequests

import com.orienteering.handrail.classes.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("users")
    fun create(@Body user: User): Call<StatusResponseEntity<User>>

    @GET("users/{id}")
    fun read(@Path("id") userID : Int) : Observable<User>

    @GET("users")
    fun readAll(): Call<List<User>>

}
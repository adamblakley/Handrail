package com.orienteering.handrail.services

import com.orienteering.handrail.classes.User
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("users")
    fun create(@Body user: User): Call<StatusResponseEntity<User>>

    @GET("users/{id}")
    fun read(@Path("id") userID : Long) : Observable<User>

    @GET("users")
    fun readAll(): Call<List<User>>

}
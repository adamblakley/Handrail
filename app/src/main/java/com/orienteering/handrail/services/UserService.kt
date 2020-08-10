package com.orienteering.handrail.services

import com.orienteering.handrail.classes.PasswordUpdateRequest
import com.orienteering.handrail.classes.User
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("users")
    fun create(@Body user: User): Call<StatusResponseEntity<User>>

    @GET("users/{id}")
    fun read(@Path("id") userId : Long) : Call<StatusResponseEntity<User>>

    @GET("users")
    fun readAll(): Call<List<User>>

    @PUT("users/{id}/update")
    fun update(@Path("id") userId : Long, @Body user : User) : Call<StatusResponseEntity<User>>

    @PUT("users/{id}/update")
    @Multipart
    fun update(@Path("id") userId : Long, @Part("user") user : User, @Part file : MultipartBody.Part?) : Call<StatusResponseEntity<User>>

    @PUT("users/{id}/update/password")
    fun updatePassword(@Path("id") userId: Long, @Body passwordUpdateRequest: PasswordUpdateRequest) : Call<StatusResponseEntity<User>>

}
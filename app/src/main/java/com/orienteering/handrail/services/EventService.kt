package com.orienteering.handrail.services

import com.orienteering.handrail.models.Event
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface EventService {

    @POST("users/{id}/events")
    @Multipart
    fun create(@Path("id") id: Long?,@Part("event") event : Event, @Part file : MultipartBody.Part): Call<StatusResponseEntity<Event>>

    @PUT("events/{id}/updatestatus")
    fun updateStatus(@Path("id") id : Int) :  Call<StatusResponseEntity<Event>>

    @GET("events/{id}")
    fun read(@Path("id") iD : Int?) : Call<StatusResponseEntity<Event>>

    @GET("users/{id}/events")
    fun readAllByUser(@Path("id") id : Long?) : Call<StatusResponseEntity<List<Event>>>

    @GET("events")
    fun readAll(): Call<StatusResponseEntity<List<Event>>>

    @GET("users/{id}/events/history")
    fun readAllByUserHistory (@Path("id") id : Long): Call<StatusResponseEntity<List<Event>>>

    @PUT("events/{id}/delete")
    fun deleteEvent(@Path("id") id : Int?) : Call<StatusResponseEntity<Boolean>>

    @PUT("events/{id}/update")
    fun update(@Path("id") eventId : Int, @Body event : Event) : Call<StatusResponseEntity<Event>>

    @POST("events/{id}/update")
    @Multipart
    fun update(@Path("id") eventId : Int, @Part("event") event : Event, @Part file : MultipartBody.Part?) : Call<StatusResponseEntity<Event>>

}
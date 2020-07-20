package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {

    @POST("events")
    fun create(@Body event: Event): Call<StatusResponseEntity<Event>>

    @GET("events/{id}")
    fun read(@Path("id") iD : Int?) : Call<Event>

    @GET("users/{id}/events")
    fun readAllByUser(@Path("id") id : Int?) : Call<List<Event>>

    @GET("events")
    fun readAll(): Call<List<Event>>

}
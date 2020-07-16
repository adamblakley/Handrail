package com.orienteering.handrail.httprequests

import com.orienteering.handrail.classes.Event
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {

    @POST("events")
    fun create(@Body event: Event): Call<StatusResponseEntity<Event>>

    @GET("events/{id}")
    fun read(@Path("id") eventID : Int?) : Call<Event>

    @GET("events")
    fun readAll(): Call<List<Event>>
}
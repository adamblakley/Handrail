package com.orienteering.handrail.services

import com.orienteering.handrail.models.Control
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ControlService {


    @POST("controls")
    fun <Control> create(@Body control: Class<Control>): Call<StatusResponseEntity<Control>>

    @POST("controls")
    fun createMany(@Body controls :List<Control>): Call<StatusResponseEntity<List<Control>>>

    @GET("controls/{id}")
    fun <Control> read(@Path("id") controlID : Int) : Observable<Control>

    @GET("controls")
    fun readAll(): Call<List<Control>>

}
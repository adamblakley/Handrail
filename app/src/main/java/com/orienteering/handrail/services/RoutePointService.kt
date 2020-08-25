package com.orienteering.handrail.services

import com.orienteering.handrail.models.RoutePoint
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Handles all service requests to REST
 *
 */
interface RoutePointService {

    @POST("participants/{id}/routepoints")
    fun create(@Path("id") participantId : Int?, @Body pcp: RoutePoint): Call<StatusResponseEntity<RoutePoint>>

    @POST("participants/{id}/routepointsmany")
    fun createMany(@Path("id") participantId : Int?, @Body pcps :List<RoutePoint>): Call<StatusResponseEntity<List<RoutePoint>>>

    @GET("routepoints/{id}")
    fun read(@Path("id") pcpID : Int) : Observable<RoutePoint>

    @GET("routepoints")
    fun readAll(): Call<List<RoutePoint>>
}
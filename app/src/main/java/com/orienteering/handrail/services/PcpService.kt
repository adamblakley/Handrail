package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Participant
import com.orienteering.handrail.classes.ParticipantControlPerformance
import com.orienteering.handrail.classes.PerformanceUploadRequest
import com.orienteering.handrail.classes.RoutePoint
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PcpService {


    @POST("events/{eventId}/users/{userId}/pcps")
    fun create(@Path("eventId") eventId : Int, @Path("userId") userId : Long, @Body request: PerformanceUploadRequest): Call<StatusResponseEntity<Participant>?>

    @GET("pcps/{id}")
    fun read(@Path("id") pcpID : Int) : Observable<ParticipantControlPerformance>

    @GET("pcps")
    fun readAll(): Call<List<ParticipantControlPerformance>>

}
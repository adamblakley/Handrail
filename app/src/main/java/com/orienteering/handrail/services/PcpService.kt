package com.orienteering.handrail.services

import com.orienteering.handrail.models.Participant
import com.orienteering.handrail.models.ParticipantControlPerformance
import com.orienteering.handrail.models.PerformanceUploadRequest
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
interface PcpService {


    @POST("events/{eventId}/users/{userId}/pcps")
    fun create(@Path("eventId") eventId : Int, @Path("userId") userId : Long, @Body request: PerformanceUploadRequest): Call<StatusResponseEntity<Participant>>

    @GET("pcps/{id}")
    fun read(@Path("id") pcpID : Int) : Observable<ParticipantControlPerformance>

    @GET("pcps")
    fun readAll(): Call<List<ParticipantControlPerformance>>

}
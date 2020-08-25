package com.orienteering.handrail.services

import com.orienteering.handrail.models.Event
import com.orienteering.handrail.models.Participant;
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.http.*

/**
 * Handles all service requests to REST
 *
 */
interface ParticipantService {
    @POST("events/{id}/participants")
    fun create(@Path("id")eventId : Int?, @Body userId : Long): Call<StatusResponseEntity<Event>>

    @POST("events/{id}/participantsmany")
    fun createMany(@Path("id")eventId : Int?, @Body participant :List<Participant>): Call<StatusResponseEntity<List<Participant>>>

    @GET("participants/{id}")
    fun read(@Path("id") participantId : Int) : Call<StatusResponseEntity<Participant>>

    @GET("participants")
    fun readAll(): Call<StatusResponseEntity<List<Participant>>>

    @GET("events/{id}/participants/top5")
    fun readTop5Participants(@Path("id") eventId : Int) : Call<StatusResponseEntity<List<Participant>>>

    @GET("events/{id}/participants")
    fun readEventParticipants(@Path("id") eventId : Int) : Call<StatusResponseEntity<List<Participant>>>

    @PUT("/events/{id}/removeparticipant")
    fun removeParticipant(@Path("id") eventId: Int, @Body userId: Long): Call<StatusResponseEntity<Event>>

    @GET("/events/{eventId}/users/{userId}/participants")
    fun getPerformance(@Path("eventId") eventId: Int, @Path("userId") userId: Long) : Call<StatusResponseEntity<Participant>>
}
package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.Participant;
import com.orienteering.handrail.classes.PerformanceResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface ParticipantService {
    @POST("events/{id}/participants")
    fun create(@Path("id")eventId : Int?, @Body userId : Long): Call<StatusResponseEntity<Event>>

    @POST("events/{id}/participantsmany")
    fun createMany(@Path("id")eventId : Int?, @Body participant :List<Participant>): Call<StatusResponseEntity<List<Participant>>>

    @GET("participants/{id}")
    fun read(@Path("id") participantId : Int) : Observable<Participant>

    @GET("participants")
    fun readAll(): Call<List<Participant>>

    @GET("events/{id}/participants/top5")
    fun readTop5Participants(@Path("id") eventId : Int) : Call<List<Participant>>

    @GET("events/{id}/participants")
    fun readEventParticipants(@Path("id") eventId : Int) : Call<List<Participant>>

    @PUT("/events/{id}/removeparticipant")
    fun removeParticipant(@Path("id") eventId: Int, @Body userId: Long): Call<StatusResponseEntity<Event>>

    @GET("/events/{eventId}/users/{userId}/participants")
    fun getPerformance(@Path("eventId") eventId: Int, @Path("userId") userId: Long) : Call<StatusResponseEntity<Participant>>
}
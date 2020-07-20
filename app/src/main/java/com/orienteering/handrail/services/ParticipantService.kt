package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Participant;
import com.orienteering.handrail.httprequests.StatusResponseEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ParticipantService {
    @POST("events/{id}/participants")
    fun create(@Path("id")eventId : Int?, @Body participant : Participant): Call<StatusResponseEntity<Participant>>

    @POST("events/{id}/participantsmany")
    fun createMany(@Path("id")eventId : Int?, @Body participant :List<Participant>): Call<StatusResponseEntity<List<Participant>>>

    @GET("participants/{id}")
    fun read(@Path("id") participantID : Int) : Observable<Participant>

    @GET("participants")
    fun readAll(): Call<List<Participant>>

    @GET("events/{id}/participants/top5")
    fun readTop5Participants(@Path("id") eventID : Int) : Call<List<Participant>>

    @GET("events/{id}/participants")
    fun readEventParticipants(@Path("id") eventID : Int) : Call<List<Participant>>
}
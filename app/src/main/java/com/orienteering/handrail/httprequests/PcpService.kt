package com.orienteering.handrail.httprequests

import com.orienteering.handrail.classes.ParticipantControlPerformance
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PcpService {

    @POST("participants/{id}/pcps")
    fun create(@Path("id") participantId : Int?, @Body pcp: ParticipantControlPerformance): Call<StatusResponseEntity<ParticipantControlPerformance>>

    @POST("participants/{id}/pcpsmany")
    fun createMany(@Path("id") participantId : Int?, @Body pcps :List<ParticipantControlPerformance>): Call<StatusResponseEntity<List<ParticipantControlPerformance>>>

    @GET("pcps/{id}")
    fun read(@Path("id") pcpID : Int) : Observable<ParticipantControlPerformance>

    @GET("pcps")
    fun readAll(): Call<List<ParticipantControlPerformance>>

}
package com.orienteering.handrail.httprequests

import com.orienteering.handrail.classes.Course
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseService {

    @POST("courses")
    fun create(@Body course: Course): Call<StatusResponseEntity<Course>>

    @GET("courses/{id}")
    fun read(@Path("id") courseID : Int) : Call<Course>

    @GET("courses")
    fun readAll(): Call<List<Course>>
}
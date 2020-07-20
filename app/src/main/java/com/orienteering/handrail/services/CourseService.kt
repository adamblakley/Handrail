package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.httprequests.StatusResponseEntity
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

    @GET("users/{id}/courses")
    fun readAllByUser(@Path("id")userID : Int): Call<List<Course>>
}
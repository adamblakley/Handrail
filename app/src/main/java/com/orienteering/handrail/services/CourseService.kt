package com.orienteering.handrail.services

import com.orienteering.handrail.models.Course
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Handles all service requests to REST
 *
 */
interface CourseService {

    @POST("courses")
    fun create(@Body course: Course): Call<StatusResponseEntity<Course>>

    @Multipart
    @POST("users/{id}/courses/upload")
    fun createWPhoto(@Path("id") id : Long, @Part("course") course: Course, @Part files : Array<MultipartBody.Part?>) : Call<StatusResponseEntity<Course>>

    @GET("courses/{id}")
    fun read(@Path("id") courseId : Int) : Call<StatusResponseEntity<Course>>

    @GET("courses")
    fun readAll(): Call<List<Course>>

    @GET("users/{id}/courses")
    fun readAllByUser(@Path("id")userID : Long): Call<StatusResponseEntity<List<Course>>>

    @PUT("courses/{id}/delete")
    fun deleteCourse(@Path("id") courseId : Int) : Call<StatusResponseEntity<Boolean>>
}
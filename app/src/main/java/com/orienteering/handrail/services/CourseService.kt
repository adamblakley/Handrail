package com.orienteering.handrail.services

import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.MultiFilesUploadRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CourseService {

    @POST("courses")
    fun create(@Body course: Course): Call<StatusResponseEntity<Course>>

    @Multipart
    @POST("users/{id}/courses/upload")
    fun createWPhoto(
        @Path("id") id : Long,
        @Part("course") course: Course,
        @Part files : Array<MultipartBody.Part?>

    ) : Call<StatusResponseEntity<Course>>

    @GET("courses/{id}")
    fun read(@Path("id") courseID : Int) : Call<Course>

    @GET("courses")
    fun readAll(): Call<List<Course>>

    @GET("users/{id}/courses")
    fun readAllByUser(@Path("id")userID : Long): Call<List<Course>>
}
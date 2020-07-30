package com.orienteering.handrail.services

import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody

import retrofit2.Call
import retrofit2.http.*

interface ImageUploadService {

    @Multipart
    @POST("/uploadeventimage")
    fun upload(@Part file : MultipartBody.Part) : Call<StatusResponseEntity<Boolean>>
}
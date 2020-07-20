package com.orienteering.handrail.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ImageUploadService {

    @Multipart
    @POST("uploadimage")
    fun upload(@Part file : MultipartBody.Part) : Call<ResponseBody>
}
package com.orienteering.handrail.controllers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.orienteering.handrail.activities.HomeActivity

import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.classes.Event
import com.orienteering.handrail.classes.MultiFilesUploadRequest
import com.orienteering.handrail.services.CourseService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseController {
    val TAG = CourseController::class.qualifiedName
    var courseService: CourseService

    init {
        courseService = ServiceFactory.makeService(CourseService::class.java)
    }

    fun uploadCourse(id : Long,course : Course, files : Array<MultipartBody.Part?>, callback : Callback<StatusResponseEntity<Course>>){
        val call = courseService.createWPhoto(id,course,files)
        call.enqueue(callback)
    }


    fun retreive(callback: Callback<List<Course>>) {
        val call = courseService.readAll()
        call.enqueue(callback)
    }

    fun retrieveAllByUser(userId: Long, callback: Callback<List<Course>?>){
        val call = courseService.readAllByUser(userId)
        call.enqueue(callback)
    }

}
package com.orienteering.handrail.controllers

import com.orienteering.handrail.classes.Course
import com.orienteering.handrail.services.CourseService
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.httprequests.StatusResponseEntity
import okhttp3.MultipartBody
import retrofit2.Callback


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

    fun retrieve(id : Int, callback : Callback<StatusResponseEntity<Course>>){
        val call = courseService.read(id)
        call.enqueue(callback)
    }

    fun retreiveAll(callback: Callback<List<Course>>) {
        val call = courseService.readAll()
        call.enqueue(callback)
    }

    fun retrieveAllByUser(userId: Long, callback: Callback<StatusResponseEntity<List<Course>?>>){
        val call = courseService.readAllByUser(userId)
        call.enqueue(callback)
    }

    fun deleteCourse(id : Int, callback: Callback<StatusResponseEntity<Boolean>>){
        val call = courseService.deleteCourse(id)
        call.enqueue(callback)
    }
}
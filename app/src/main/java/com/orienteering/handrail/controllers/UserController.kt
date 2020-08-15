package com.orienteering.handrail.controllers

import com.orienteering.handrail.models.PasswordUpdateRequest
import com.orienteering.handrail.models.User
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.services.UserService
import okhttp3.MultipartBody
import retrofit2.Callback

/**
 * Calls UserService, returns responses via Callback
 */
class UserController {

    // TAG for Log
    val TAG: String = "UserController"
    //User Service handle requests for user
    var userService : UserService

    /**
     * Initialise userservice via service factory
     */
    init {
        userService = ServiceFactory.makeService(UserService::class.java)
    }

    /**
     * Get User by ID
     *
     * @param userId
     * @param callback
     */
    fun read(userId: Long,callback: Callback<StatusResponseEntity<User>?>){
        val call = userService.read(userId)
        call.enqueue(callback)
    }


    /**
     * Function to call service method to update user
     *
     * @param userId
     * @param user
     * @param callback
     */
    fun update(userId: Long,user : User, callback : Callback<StatusResponseEntity<User>?>){
        val call = userService.update(userId,user)
        call.enqueue(callback)
    }


    /**
     * Function to call service method to update user
     *
     * @param userId
     * @param user
     * @param file
     * @param callback
     */
    fun update(userId: Long,user : User, file : MultipartBody.Part?, callback : Callback<StatusResponseEntity<User>?>){
        val call = userService.update(userId,user,file)
        call.enqueue(callback)
    }

    /**
     * Function to callservice method to update user password
     * @param userId
     * @param passwordUpdateRequest
     * @param callback
     */
    fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest, callback: Callback<StatusResponseEntity<Boolean>>){
        val call = userService.updatePassword(userId,passwordUpdateRequest)
        call.enqueue(callback)
    }
}
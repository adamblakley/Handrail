package com.orienteering.handrail.controllers

import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.ServiceFactory
import com.orienteering.handrail.services.SignupService
import retrofit2.Callback

/**
 * Controls signup service ofr signup requests
 *
 */
class SignupController {

    val TAG: String = "SignupController"
    var signupService : SignupService

    init{
        signupService = ServiceFactory.makeService(SignupService::class.java)
    }

    /**
     * login request, accepts loginRequest and responds successfully with callback to calling method
     * @param loginRequest
     */
    fun signup(signupRequest: SignupRequest, callback: Callback<StatusResponseEntity<Boolean>>){
        val call = signupService.signup(signupRequest)
        call.enqueue(callback)
    }
}
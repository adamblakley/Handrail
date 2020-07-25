package com.orienteering.handrail.controllers

import android.util.Log
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.LoginService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Control login service requests
 *
 */
class LoginController {
    val TAG: String = "LoginController"
    var loginService : LoginService

    init{
        loginService = ServiceFactory.makeService(LoginService::class.java)
    }

    /**
     * login request, accepts loginRequest and responds successfully with callback to calling method
     * @param loginRequest
     */
    fun login(loginRequest:LoginRequest, callback: Callback<StatusResponseEntity<LoginResponse>>){
        val call = loginService.login(loginRequest)
        call.enqueue(callback)
    }
}
package com.orienteering.handrail.controllers

import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.services.LoginService
import com.orienteering.handrail.services.ServiceFactory
import retrofit2.Callback


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

    /**
     * Check user is currently authenticated
     *
     * @param callback
     */
    fun checkLogin(callback: Callback<StatusResponseEntity<Boolean>>){
        val call = loginService.checkLoggedIn()
        call.enqueue(callback)
    }
}
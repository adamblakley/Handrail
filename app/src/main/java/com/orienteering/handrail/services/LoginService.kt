package com.orienteering.handrail.services


import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.Field
import retrofit2.http.POST

/**
 * Inteface Login Service, facilitate login requests
 */
interface LoginService {


    /**
     * pass login request, receive response as statusresponseentity<loginresponse>
     * @param String userEmail
     * @param String password
     * @return Call<StatusResponseEntity<LoginResponse>>
     */
    @POST("authentication/login")
    fun login(@Body loginRequest: LoginRequest) : Call<StatusResponseEntity<LoginResponse>>
}
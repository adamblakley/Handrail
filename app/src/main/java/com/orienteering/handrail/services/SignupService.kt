package com.orienteering.handrail.services

import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface signup requests
 *
 */
interface SignupService {



    /**
     * Sends signup request, returns respose
     *
     * @param signupRequest
     * @return
     */
    @POST("authentication/signup")
    fun signup(@Body signupRequest: SignupRequest) : Call<StatusResponseEntity<Boolean>>
}
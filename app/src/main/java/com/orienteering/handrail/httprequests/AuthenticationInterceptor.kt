package com.orienteering.handrail.httprequests

import com.orienteering.handrail.utilities.App
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Implements Interceptor
 * Adds Auth token header to requests
 *
 */
class AuthenticationInterceptor : Interceptor {

    /**
     * Creates a response, if response status is 401, divert to unauthoizedhandler redirect
     *
     * @param chain
     * @return
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // add header of user authentication token
        var request : Request = chain.request().newBuilder()?.addHeader("Authorization","Bearer ${App.sharedPreferences.getString(App.SharedPreferencesAuthToken,"0")}")?.build()
        // capture response
        var response = chain.proceed(request)
        // check if access denied
        if (response.code==401){
            // redirect via unauthorized handler
            val unauthorizedHandler = UnauthorizedHandler()
            unauthorizedHandler.redirect()
        }
        return response
    }
}
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

    override fun intercept(chain: Interceptor.Chain): Response {
        var request : Request = chain.request().newBuilder()?.addHeader("Authorization","Bearer ${App.sharedPreferences.getString(App.SharedPreferencesAuthToken,"0")}")?.build()

        var response = chain.proceed(request)
        if (response.code==401){
            val unauthorizedHandler =
                UnauthorizedHandler()
            unauthorizedHandler.redirect()
        }
        return response
    }
}
package com.orienteering.handrail.httprequests

import retrofit2.Response

/**
 * Handle responses from web-service layer
 *
 * @param T
 */
interface IOnFinishedListener<T> {
    /**
     * On response received
     *
     * @param response
     */
    fun onFinished(response: Response<StatusResponseEntity<T>>)

    /**
     * On response failure
     *
     * @param t
     */
    fun onFailure(t : Throwable)
}
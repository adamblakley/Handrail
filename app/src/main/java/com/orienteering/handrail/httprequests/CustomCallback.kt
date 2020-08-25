package com.orienteering.handrail.httprequests

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Custom callback class implements Callback
 *
 * Routes onFailure and onResponse to correct listener methods OnFailure and OnFinished to handle response
 *
 * @param T
 * @constructor
 *
 * @param onFinishedListener
 */
class CustomCallback<T>(onFinishedListener: IOnFinishedListener<T>) : Callback<StatusResponseEntity<T>> {

    // Handles responses
    var onFinishedListener : IOnFinishedListener<T>

    /**
     * Initialise listener
     */
    init{
        this.onFinishedListener = onFinishedListener
    }

    /**
     * calls listener onfailure
     *
     * @param call
     * @param t
     */
    override fun onFailure(call: Call<StatusResponseEntity<T>>, t: Throwable) {
        onFinishedListener.onFailure(t)
    }

    /**
     * calls listener onfinished
     *
     * @param call
     * @param response
     */
    override fun onResponse(call: Call<StatusResponseEntity<T>>, response: Response<StatusResponseEntity<T>>) {
        onFinishedListener.onFinished(response)
    }

}
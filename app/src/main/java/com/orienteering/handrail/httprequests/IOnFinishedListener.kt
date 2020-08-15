package com.orienteering.handrail.httprequests

import retrofit2.Response

interface IOnFinishedListener<T> {
    fun onFinished(response: Response<StatusResponseEntity<T>>)
    fun onFailure(t : Throwable)
}
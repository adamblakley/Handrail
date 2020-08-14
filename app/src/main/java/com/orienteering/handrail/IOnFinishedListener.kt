package com.orienteering.handrail

import com.orienteering.handrail.httprequests.StatusResponseEntity
import retrofit2.Response

interface IOnFinishedListener<T> {
    fun onFinished(response: Response<StatusResponseEntity<T>>)
    fun onFailure(t : Throwable)
}
package com.orienteering.handrail.password_update

import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.models.PasswordUpdateRequest

interface IPasswordUpdateContract {

    interface IPasswordUpdateView{

        fun onResponseFailure(throwable : Throwable)
        fun onResponsePasswordError()
        fun onResponseError()
        fun onResponseSuccess()

    }

    interface IPasswordUpdatePerformer{
        fun onDestroy()
        fun putDataToServer(passwordUpdateRequest: PasswordUpdateRequest)
    }

}
package com.orienteering.handrail.signup

import com.orienteering.handrail.httprequests.SignupRequest

interface ISignupContract {

    interface ISignupPerformer{
        fun onDestroy()
        fun postDataToServer(signupRequest: SignupRequest)
    }

    interface ISignupView{
        fun validateFields() : Boolean
        fun emailInUse()
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun startLoginActivity()
        fun createEditText()
        fun createButtons()
        fun makeToast(message : String)
    }

}
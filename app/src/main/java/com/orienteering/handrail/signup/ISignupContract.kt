package com.orienteering.handrail.signup

interface ISignupContract {

    interface ISignupPerformer{
        fun onDestroy()
        fun requestDataFromServer(email : String, password : String)
        fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long)
    }

    interface ISignupView{
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun startHomeMenuActivity()
        fun createEditText()
        fun createButtons()
        fun makeToast(message : String)
    }

}
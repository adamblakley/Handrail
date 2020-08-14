package com.orienteering.handrail.login

interface ILoginContract {

    interface ILoginPerformer{
        fun onDestroy()
        fun requestDataFromServer(email : String, password : String)
        fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long)
    }

    interface ILoginView{
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
        fun startHomeMenuActivity()
        fun createEditText()
        fun createButtons()
        fun makeToast(message : String)
    }
}
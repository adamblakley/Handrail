package com.orienteering.handrail.welcome

interface IWelcomeContract {

    interface IWelcomeView{
        fun onResponseSuccess()
        fun onResponseFailure(throwable : Throwable)
        fun onResponseError()
    }

    interface IWelcomePerformer{
        fun checkLogin()
        fun onDestroy()
    }

}
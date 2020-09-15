package com.orienteering.handrail.splash

interface ISplashContract {

    interface ISplashPresenter{
        fun getData()
    }

    interface ISplashView{
        fun onResponseSuccess()

        fun onResponseFailure()

        fun onresponseError()
    }

}
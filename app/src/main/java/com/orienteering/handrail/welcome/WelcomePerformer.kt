package com.orienteering.handrail.welcome

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import retrofit2.Response

class WelcomePerformer(welcomeView: IWelcomeContract.IWelcomeView, loginInteractor: LoginInteractor) : IWelcomeContract.IWelcomePerformer {

    var welcomeView : IWelcomeContract.IWelcomeView?
    var loginInteractor : LoginInteractor
    var checkLoginOnFinishedListener : IOnFinishedListener<Boolean>

    init{
        this.welcomeView=welcomeView
        this.loginInteractor=loginInteractor
        checkLoginOnFinishedListener = CheckLoginOnFinishedListener(welcomeView,this)
    }

    override fun checkLogin() {
        loginInteractor.checkLogin(checkLoginOnFinishedListener)
    }

    override fun onDestroy() {
        welcomeView=null
    }
}

class CheckLoginOnFinishedListener(welcomeView : IWelcomeContract.IWelcomeView, welcomePerformer: IWelcomeContract.IWelcomePerformer) :
    IOnFinishedListener<Boolean> {

    private var welcomeView : IWelcomeContract.IWelcomeView
    private var welcomePerformer : IWelcomeContract.IWelcomePerformer

    init{
        this.welcomeView = welcomeView
        this.welcomePerformer = welcomePerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                if (response.body()?.entity==true){
                    welcomeView.onResponseSuccess()
                } else if (response.code()==403) {
                    welcomeView?.onResponseError()
                }
            } else {
                welcomeView?.onResponseError()
            }
        } else {
            welcomeView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (welcomeView!=null){
            welcomeView?.onResponseFailure(t)
        }
    }
}
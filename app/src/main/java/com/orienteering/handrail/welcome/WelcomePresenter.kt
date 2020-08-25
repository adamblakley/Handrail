package com.orienteering.handrail.welcome

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import retrofit2.Response

class WelcomePresenter(welcomeView: IWelcomeContract.IWelcomeView, loginInteractor: LoginInteractor) : IWelcomeContract.IWelcomePresenter {

    private var welcomeView : IWelcomeContract.IWelcomeView?
    private var loginInteractor : LoginInteractor
    private var checkLoginOnFinishedListener : IOnFinishedListener<Boolean>

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

class CheckLoginOnFinishedListener(welcomeView : IWelcomeContract.IWelcomeView, welcomePresenter: IWelcomeContract.IWelcomePresenter) : IOnFinishedListener<Boolean> {

    private var welcomeView : IWelcomeContract.IWelcomeView = welcomeView
    private var welcomePresenter : IWelcomeContract.IWelcomePresenter = welcomePresenter

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
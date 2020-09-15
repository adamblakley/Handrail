package com.orienteering.handrail.splash

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import retrofit2.Response

class SplashPresenter(view: ISplashContract.ISplashView) : ISplashContract.ISplashPresenter {

    var view : ISplashContract.ISplashView
    var onFinishedListener : CheckLoginOnFinishedListener

    init{
        this.view=view
        this.onFinishedListener=CheckLoginOnFinishedListener(view,this)
    }

    override fun getData() {
        val interactor = LoginInteractor()
        interactor.checkLogin(onFinishedListener)
    }
}

class CheckLoginOnFinishedListener(view : ISplashContract.ISplashView, presenter: ISplashContract.ISplashPresenter) :
    IOnFinishedListener<Boolean> {

    private var view : ISplashContract.ISplashView = view
    private var presenter : ISplashContract.ISplashPresenter = presenter

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                if (response.body()?.entity==true){
                    view.onResponseSuccess()
                } else if (response.code()==403) {
                    view?.onResponseFailure()
                }
            } else {
                view?.onResponseFailure()
            }
        } else {
            view?.onResponseFailure()
        }
    }

    override fun onFailure(t: Throwable) {
        if (view!=null){
            view?.onresponseError()
        }
    }
}
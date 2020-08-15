package com.orienteering.handrail.signup

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.SignupInteractor
import retrofit2.Response

class SignupPerformer(signupView : ISignupContract.ISignupView, signupnteractor: SignupInteractor) : ISignupContract.ISignupPerformer {

    var signupView : ISignupContract.ISignupView?
    var signupnteractor : SignupInteractor
    var signupOnFinishedListener = SignupOnFinishedListener(this,signupView)

    init{
        this.signupView=signupView
        this.signupnteractor=signupnteractor
    }

    override fun postDataToServer(signupRequest: SignupRequest) {
        signupnteractor.signup(signupRequest,signupOnFinishedListener)
    }

    override fun onDestroy() {
        signupView = null
    }
}

class SignupOnFinishedListener(signupPerformer : ISignupContract.ISignupPerformer, signupView: ISignupContract.ISignupView) :
    IOnFinishedListener<Boolean> {

    private var signupView : ISignupContract.ISignupView
    private var signupPerformer : ISignupContract.ISignupPerformer

    init{
        this.signupView = signupView
        this.signupPerformer = signupPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                if(response.body()?.entity==true){
                    signupView.makeToast("Successful Account Creation, please log in")
                    signupView.startLoginActivity()
                }

            } else {
                signupView?.onResponseError()
            }
        } else {
            if (response.code()==409){
                signupView.emailInUse()
            } else {
                signupView?.onResponseError()
            }
        }
    }

    override fun onFailure(t: Throwable) {
        if (signupView!=null){
            signupView?.onResponseFailure(t)
        }
    }
}
package com.orienteering.handrail.signup

import com.orienteering.handrail.IOnFinishedListener
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.SignupInteractor
import com.orienteering.handrail.login.ILoginContract
import retrofit2.Response

class SignupPerformer(signupView : ISignupContract.ISignupView, signupnteractor: SignupInteractor) : ISignupContract.ISignupPerformer {

    var signupView : ISignupContract.ISignupView?
    var signupnteractor : SignupInteractor

    init{
        this.signupView=signupView
        this.signupnteractor=signupnteractor
    }

    override fun requestDataFromServer(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun insertSharedPreferences(authToken: String, tokenType: String, userId: Long) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        signupView = null
    }
}

class SignupOnFinishedListener(signupPerformer : ISignupContract.ISignupPerformer, signupView: ILoginContract.ILoginView) :
    IOnFinishedListener<LoginResponse> {

    private var signupView : ISignupContract.ISignupView
    private var signupPerformer : ISignupContract.ISignupPerformer

    init{
        this.signupView = signupView
        this.signupPerformer = signupPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<LoginResponse>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                val loginResponse : LoginResponse? = response.body()?.entity
                if (loginResponse != null) {
                    signupPerformer.insertSharedPreferences(loginResponse.accessToken,loginResponse.tokenType,loginResponse.userId)
                }
                signupView.makeToast("Successful login")
                signupView.startHomeMenuActivity()
            } else {
                signupView?.onResponseError()
            }
        } else {
            signupView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (signupView!=null){
            signupView?.onResponseFailure(t)
        }
    }
}
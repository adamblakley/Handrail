package com.orienteering.handrail.login

import com.orienteering.handrail.IOnFinishedListener
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.App.AppCompanion.sharedPreferences
import retrofit2.Response

class LoginPerformer(loginView : ILoginContract.ILoginView, loginInteractor: LoginInteractor) : ILoginContract.ILoginPerformer {

    var loginView : ILoginContract.ILoginView?
    var loginInteractor : LoginInteractor
    var loginOnFinishedListener : LoginOnFinishedListener

    init{
        this.loginView=loginView
        this.loginInteractor = loginInteractor
        this.loginOnFinishedListener = LoginOnFinishedListener(this,loginView)
    }

    override fun requestDataFromServer(email: String, password: String) {
        if (email!=null && password!=null || email.trim().length>0 && password.trim().length>0){
            val loginRequest = LoginRequest(email,password)
            loginInteractor.login(loginRequest,loginOnFinishedListener)
        }
    }

    override fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long) {
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(App.SharedPreferencesAuthToken,authToken).commit()
        sharedPreferencesEditor.putString(App.SharedPreferencesTokenType,tokenType).commit()
        sharedPreferencesEditor.putLong(App.SharedPreferencesUserId,userId).commit()
    }

    override fun onDestroy() {
        loginView = null
    }
}

class LoginOnFinishedListener(loginPerformer : ILoginContract.ILoginPerformer, loginView: ILoginContract.ILoginView) : IOnFinishedListener<LoginResponse> {

    private var loginView : ILoginContract.ILoginView
    private var loginPerformer : ILoginContract.ILoginPerformer

    init{
        this.loginView = loginView
        this.loginPerformer = loginPerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<LoginResponse>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                val loginResponse : LoginResponse? = response.body()?.entity
                if (loginResponse != null) {
                    loginPerformer.insertSharedPreferences(loginResponse.accessToken,loginResponse.tokenType,loginResponse.userId)
                }
                loginView.makeToast("Successful login")
                loginView.startHomeMenuActivity()
            } else {
                loginView?.onResponseError()
            }
        } else {
            loginView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (loginView!=null){
            loginView?.onResponseFailure(t)
        }
    }
}
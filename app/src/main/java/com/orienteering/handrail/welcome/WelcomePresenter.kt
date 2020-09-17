package com.orienteering.handrail.welcome

import android.util.Log
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import com.orienteering.handrail.utilities.App
import retrofit2.Response

class WelcomePresenter(welcomeView: IWelcomeContract.IWelcomeView, loginInteractor: LoginInteractor) : IWelcomeContract.IWelcomePresenter {

    private var welcomeView : IWelcomeContract.IWelcomeView?
    private var loginInteractor : LoginInteractor
    // provides output for logininteractor
    var loginPostOnFinishedListener : LoginPostOnFinishedListener

    init{
        this.welcomeView=welcomeView
        this.loginInteractor=loginInteractor
        this.loginPostOnFinishedListener = LoginPostOnFinishedListener(welcomeView,this)
    }

    override fun checkLogin() {
    }

    /**
     * Utiilise interactor to send login request for response
     * Check email and password variables
     * @param email
     * @param password
     */
    override fun requestDataFromServer(email: String, password: String) {
        if (email!=null && password!=null || email.trim().length>0 && password.trim().length>0){
            val loginRequest = LoginRequest(email,password)
            loginInteractor.login(loginRequest,loginPostOnFinishedListener)
        }
    }

    /**
     * Save authoirzation, user id and token type to shared preferences to be retrieved on subsequent rest calls by interceptor
     *
     * @param authToken
     * @param tokenType
     * @param userId
     */
    override fun insertSharedPreferences(authToken : String, tokenType : String,userId : Long) {
        val sharedPreferencesEditor = App.sharedPreferences.edit()
        sharedPreferencesEditor.putString(App.SharedPreferencesAuthToken,authToken).commit()
        sharedPreferencesEditor.putString(App.SharedPreferencesTokenType,tokenType).commit()
        sharedPreferencesEditor.putLong(App.SharedPreferencesUserId,userId).commit()
    }

    override fun onDestroy() {
        welcomeView=null
    }
}



/**
 * Responsible for handling login response from interactor callback
 *
 * @constructor
 *
 * @param loginPresenter
 * @param loginView
 */
class LoginPostOnFinishedListener(welcomeView : IWelcomeContract.IWelcomeView, welcomePresenter: IWelcomeContract.IWelcomePresenter) : IOnFinishedListener<LoginResponse> {

    private var loginView : IWelcomeContract.IWelcomeView = welcomeView
    private var loginPresenter : IWelcomeContract.IWelcomePresenter = welcomePresenter

    /**
     * call presenter to insert authorization to shared preferences, redirect user
     *
     * @param response
     */
    override fun onFinished(response: Response<StatusResponseEntity<LoginResponse>>) {
        if(response.isSuccessful){
            if (response.body()?.entity != null) {
                val loginResponse : LoginResponse? = response.body()?.entity
                if (loginResponse != null) {
                    loginPresenter.insertSharedPreferences(loginResponse.accessToken,loginResponse.tokenType,loginResponse.userId)
                }
                loginView.makeToast("Successful login")
                loginView.startHomeMenuActivity()
            } else if(response.code()==403){
                loginView?.onResponseIncorrect()
            } else {
                loginView?.onResponseError()
            }
        } else if(response.code()==403){
            loginView?.onResponseIncorrect()
        }  else {
            loginView?.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (loginView!=null){
            loginView?.onResponseFailure(t)
        }
    }
}
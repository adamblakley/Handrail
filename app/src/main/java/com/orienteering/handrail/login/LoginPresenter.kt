package com.orienteering.handrail.login

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.LoginRequest
import com.orienteering.handrail.httprequests.LoginResponse
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.LoginInteractor
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.App.AppCompanion.sharedPreferences
import retrofit2.Response

/**
 * Handles all logic and events for user login
 *
 * @constructor
 *
 * @param loginView
 * @param loginInteractor
 */
class LoginPresenter(loginView : ILoginContract.ILoginView, loginInteractor: LoginInteractor) : ILoginContract.ILoginPresenter {

    // view for user interface
    var loginView : ILoginContract.ILoginView?
    // interactor to retrieve login information
    var loginInteractor : LoginInteractor
    // provides output for logininteractor
    var loginOnFinishedListener : LoginOnFinishedListener

    /**
     * Initialise variables
     */
    init{
        this.loginView=loginView
        this.loginInteractor = loginInteractor
        this.loginOnFinishedListener = LoginOnFinishedListener(this,loginView)
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
            loginInteractor.login(loginRequest,loginOnFinishedListener)
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
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(App.SharedPreferencesAuthToken,authToken).commit()
        sharedPreferencesEditor.putString(App.SharedPreferencesTokenType,tokenType).commit()
        sharedPreferencesEditor.putLong(App.SharedPreferencesUserId,userId).commit()
    }

    override fun onDestroy() {
        loginView = null
    }
}

/**
 * Responsible for handling login response from interactor callback
 *
 * @constructor
 * TODO
 *
 * @param loginPresenter
 * @param loginView
 */
class LoginOnFinishedListener(loginPresenter : ILoginContract.ILoginPresenter, loginView: ILoginContract.ILoginView) : IOnFinishedListener<LoginResponse> {

    private var loginView : ILoginContract.ILoginView = loginView
    private var loginPresenter : ILoginContract.ILoginPresenter = loginPresenter

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
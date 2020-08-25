package com.orienteering.handrail.signup

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.SignupRequest
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.SignupInteractor
import retrofit2.Response

/**
 * Signup presenter responsible for handling all signup requests
 *
 * @constructor
 *
 * @param signupView
 * @param signupnteractor
 */
class SignupPresenter(signupView : ISignupContract.ISignupView, signupnteractor: SignupInteractor) : ISignupContract.ISignupPresenter {

    // view, interactor and onfininished listener values
    var signupView : ISignupContract.ISignupView?
    private var signupnteractor : SignupInteractor
    var signupOnFinishedListener = SignupOnFinishedListener(signupView)

    /**
     * initiate interactor and view
     */
    init{
        this.signupView=signupView
        this.signupnteractor=signupnteractor
    }

    /**
     * Use interactor to load signup request at source
     *
     * @param signupRequest
     */
    override fun postDataToServer(signupRequest: SignupRequest) {
        signupnteractor.signup(signupRequest,signupOnFinishedListener)
    }

    override fun onDestroy() {
        signupView = null
    }
}

/**
 * Class responsible for handling signup request responses
 *
 * @constructor
 *
 * @param signupPresenter
 * @param signupView
 */

class SignupOnFinishedListener(signupView: ISignupContract.ISignupView) : IOnFinishedListener<Boolean> {

    private var signupView : ISignupContract.ISignupView = signupView

    /**
     * On success, request login activity
     *
     * @param response
     */
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
            // if error is conflic, respond that email is in use
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
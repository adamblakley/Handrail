package com.orienteering.handrail.password_update

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.PasswordUpdateRequest
import com.orienteering.handrail.utilities.App
import retrofit2.Response

/**
 * Presenter handles password update logic and requests to source
 *
 * @constructor
 *
 * @param passwordUpdateView
 * @param userInteractor
 */
class PasswordUpdatePresenter(passwordUpdateView: IPasswordUpdateContract.IPasswordUpdateView, userInteractor: UserInteractor) : IPasswordUpdateContract.IPasswordUpdatePresenter {

    // view for display and user interaction
    private var passwordUpdateView : IPasswordUpdateContract.IPasswordUpdateView?
    // interactor requests data from source
    private var userInteractor : UserInteractor
    // handles retrieved data
    private var passwordOnFinishedListener = PasswordOnFinishedListener(this,passwordUpdateView)

    /**
     * Initialise view and interactor
     */
    init{
        this.passwordUpdateView=passwordUpdateView
        this.userInteractor=userInteractor
    }

    /**
     * Utilises interactor to request password update via passwordupdaterequest object
     *
     * @param passwordUpdateRequest
     */
    override fun putDataToServer(passwordUpdateRequest: PasswordUpdateRequest) {
        userInteractor.updatePassword(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),passwordUpdateRequest,passwordOnFinishedListener)
    }

    override fun onDestroy() {
        passwordUpdateView = null
    }
}

/**
 * Responsible for handling success or failure of response for password update
 *
 * @constructor
 *
 * @param passwordUpdatePresenter
 * @param passwordUpdateView
 */
class PasswordOnFinishedListener(passwordUpdatePresenter: IPasswordUpdateContract.IPasswordUpdatePresenter, passwordUpdateView: IPasswordUpdateContract.IPasswordUpdateView) : IOnFinishedListener<Boolean> {

    // passed view and presenter
    private var passwordUpdateView : IPasswordUpdateContract.IPasswordUpdateView = passwordUpdateView
    private var passwordUpdatePresenter : IPasswordUpdateContract.IPasswordUpdatePresenter = passwordUpdatePresenter

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity ==true) {
                passwordUpdateView.onResponseSuccess()
            } else{
                passwordUpdateView.onResponseError()
            }
            // check if http response code is 403 - tailor response to user
        } else if (response.code()==403){
            passwordUpdateView.onResponsePasswordError()
        } else {
            passwordUpdateView.onResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (passwordUpdateView!=null){
            passwordUpdateView.onResponseFailure(t)
        }
    }
}
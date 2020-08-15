package com.orienteering.handrail.password_update

import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.PasswordUpdateRequest
import com.orienteering.handrail.models.User
import com.orienteering.handrail.utilities.App
import retrofit2.Response

class PasswordUpdatePerformer(passwordUpdateView: IPasswordUpdateContract.IPasswordUpdateView, userInteractor: UserInteractor) : IPasswordUpdateContract.IPasswordUpdatePerformer {

    var passwordUpdateView : IPasswordUpdateContract.IPasswordUpdateView
    var userInteractor : UserInteractor
    var passwordOnFinishedListener = PasswordOnFinishedListener(this,passwordUpdateView)

    init{
        this.passwordUpdateView=passwordUpdateView
        this.userInteractor=userInteractor
    }

    override fun putDataToServer(passwordUpdateRequest: PasswordUpdateRequest) {
        userInteractor.updatePassword(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),passwordUpdateRequest,passwordOnFinishedListener)
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }
}

class PasswordOnFinishedListener(passwordUpdatePerformer: IPasswordUpdateContract.IPasswordUpdatePerformer, passwordUpdateView: IPasswordUpdateContract.IPasswordUpdateView) : IOnFinishedListener<Boolean> {

    private var passwordUpdateView : IPasswordUpdateContract.IPasswordUpdateView
    private var passwordUpdatePerformer : IPasswordUpdateContract.IPasswordUpdatePerformer

    init{
        this.passwordUpdateView = passwordUpdateView
        this.passwordUpdatePerformer = passwordUpdatePerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<Boolean>>) {
        if(response.isSuccessful){
            if (response.body()?.entity ==true) {
                passwordUpdateView.onResponseSuccess()
            } else{
                passwordUpdateView.onResponseError()
            }
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
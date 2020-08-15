package com.orienteering.handrail.edit_profile

import android.net.Uri
import com.orienteering.handrail.models.User

interface IEditProfileContract {

    interface IEditProfileView{
        fun onGetResponseFailure(throwable : Throwable)
        fun onGetResponseError()
        fun onUpdateResponseFailure(throwable : Throwable)
        fun onUpdateResponseError()
        fun onUpdatePartialResponseError()
        fun onUpdateResponseSuccess()
        fun fillInformation(firstName : String, lastName : String, email : String, bio : String, dob : String)
        fun setupImage(imageUrl : String)
    }

    interface IEditProfilePerformer{
        fun onDestroy()
        fun putDataOnServer(user : User)
        fun getDataFromServer()
        fun selectImage()
        fun setUser(user : User)
        fun setImage(imageUri : Uri)
    }

}
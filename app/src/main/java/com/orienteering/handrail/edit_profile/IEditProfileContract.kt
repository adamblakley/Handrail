package com.orienteering.handrail.edit_profile

import android.net.Uri
import com.orienteering.handrail.models.User

/**
 * Contract between view and presenter of edit profile use case
 *
 */
interface IEditProfileContract {

    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface IEditProfileView{
        /**
         * Provide user feedback of connectivity error for profile request
         *
         * @param throwable
         */
        fun onGetResponseFailure(throwable : Throwable)

        /**
         * Provide user feedback of retieval error for profile request
         *
         */
        fun onGetResponseError()

        /**
         * Provide user feedback of connectivity error for update request
         *
         * @param throwable
         */
        fun onUpdateResponseFailure(throwable : Throwable)

        /**
         * Provide user feedback of update error for update request
         *
         */
        fun onUpdateResponseError()

        /**
         * Provide user feedback of partial update success for update request
         *
         */
        fun onUpdatePartialResponseError()

        /**
         * Provide user feedback on successful update request response
         *
         */
        fun onUpdateResponseSuccess()

        /**
         * Display user profile information
         *
         * @param firstName
         * @param lastName
         * @param email
         * @param bio
         * @param dob
         */
        fun fillInformation(firstName : String, lastName : String, email : String, bio : String, dob : String)
        /**
         * set image uri for profile image
         *
         * @param imageUri
         */
        fun setupImage(imageUrl : String)
    }

    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface IEditProfilePresenter{
        /**
         * Destroy view
         *
         */
        fun onDestroy()

        /**
         * post user data to backend
         *
         * @param user
         */
        fun putDataOnServer(user : User)

        /**
         * retrieve user data from backend
         *
         */
        fun getDataFromServer()

        /**
         * initiate select image from gallery or camera
         *
         */
        fun selectImage()

        /**
         * set user of presenter
         *
         * @param user
         */
        fun setUser(user : User)

        /**
         * set image uri for profile image
         *
         * @param imageUri
         */
        fun setImage(imageUri : Uri)
    }

}
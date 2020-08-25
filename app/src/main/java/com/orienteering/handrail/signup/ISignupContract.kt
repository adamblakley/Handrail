package com.orienteering.handrail.signup

import com.orienteering.handrail.httprequests.SignupRequest
/**
 * Contract defines relationships between view and presenter for signup use case
 *
 */
interface ISignupContract {
    /**
     * Presenter interface, provides functions for presenter-view interaction
     *
     */
    interface ISignupPresenter{
        /**
         * destroy view on destruction of presenter
         *
         */
        fun onDestroy()

        /**
         *  post new signup request to srouce
         *
         * @param signupRequest
         */
        fun postDataToServer(signupRequest: SignupRequest)
    }
    /**
     * View interface, provides functions for presenter-view interaction
     *
     */
    interface ISignupView{


        /**
         * Respond with email is in use error
         *
         */
        fun emailInUse()

        /**
         * Handle connection failure
         *
         * @param throwable
         */
        fun onResponseFailure(throwable : Throwable)

        /**
         * Handle service error
         *
         */
        fun onResponseError()

        /**
         * initiate login use case
         *
         */
        fun startLoginActivity()

        /**
         * Display message to user
         *
         * @param message
         */
        fun makeToast(message : String)
    }

}